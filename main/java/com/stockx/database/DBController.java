package com.stockx.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.stockx.model.*;
import com.stockx.util.StringUtils;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

public class DBController {

    private static final Logger LOGGER = Logger.getLogger(DBController.class.getName());

    /**
     * Establishes a connection to the database.
     */
    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(StringUtils.DRIVER_NAME);
        return DriverManager.getConnection(StringUtils.LOCALHOST_URL, StringUtils.LOCALHOST_USERNAME, StringUtils.LOCALHOST_PASSWORD);
    }

    // --- Password & User Existence Checks ---

    /**
     * Checks if an email already exists in the Password table.
     * @param email The email to check.
     * @return True if the email exists, false otherwise.
     */
    public boolean checkEmailExists(String email) {
        String sql = StringUtils.QUERY_CHECK_EMAIL_EXISTS;
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error checking if email exists: " + email, e);
        }
        return false; // Assume not exists on error for safety, or handle differently
    }

    /**
     * Retrieves password details (ID, hash, role) for a given email.
     * @param email The user's email.
     * @return PasswordModel containing details, or null if not found or error.
     */
    public PasswordModel getPasswordInfoByEmail(String email) {
        String sql = StringUtils.QUERY_GET_PASSWORD_INFO_BY_EMAIL;
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PasswordModel passModel = new PasswordModel();
                    passModel.setPasswordId(rs.getInt("Password_Id"));
                    passModel.setPasswordHash(rs.getString("Password_Hash"));
                    passModel.setUserRole(rs.getString("User_Role"));
                    passModel.setEmail(email); // Set email back for completeness
                    return passModel;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error getting password info for email: " + email, e);
        }
        return null;
    }


    // --- Registration ---

    /**
     * Registers a new customer, inserting records into Password and Customer tables.
     * Uses a transaction to ensure atomicity.
     * @param customer The CustomerModel object containing registration data.
     * @param plainPassword The plain text password for hashing.
     * @return 1 for success, 0 for failure (e.g., email exists), -1 for server error.
     */
    public int registerCustomer(CustomerModel customer, String plainPassword) {
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false); // Start transaction

            // 1. Check if email already exists
            if (checkEmailExists(customer.getCustomerEmail())) {
                 con.rollback(); // Rollback transaction
                 LOGGER.warning("Registration attempt failed: Email already exists - " + customer.getCustomerEmail());
                 return 0; // Indicate email exists failure
            }

            // 2. Encrypt password
            String hashedPassword = PasswordEncryptionWithAes.encrypt(plainPassword, customer.getCustomerEmail());
            if (hashedPassword == null) {
                con.rollback();
                LOGGER.severe("Password encryption failed for: " + customer.getCustomerEmail());
                return -1; // Indicate server error
            }

            // 3. Insert into Password table
            int passwordId = -1;
            String sqlPassword = StringUtils.QUERY_INSERT_PASSWORD;
            try (PreparedStatement stmtPass = con.prepareStatement(sqlPassword, Statement.RETURN_GENERATED_KEYS)) {
                stmtPass.setString(1, customer.getCustomerEmail());
                stmtPass.setString(2, hashedPassword);
                stmtPass.setString(3, StringUtils.ROLE_CUSTOMER); // Set role explicitly

                int rowsAffected = stmtPass.executeUpdate();
                if (rowsAffected == 0) {
                    con.rollback();
                    LOGGER.severe("Failed to insert into Password table for: " + customer.getCustomerEmail());
                    return -1;
                }

                // Get generated Password_Id
                try (ResultSet generatedKeys = stmtPass.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        passwordId = generatedKeys.getInt(1);
                    } else {
                        con.rollback();
                         LOGGER.severe("Failed to retrieve generated Password_Id for: " + customer.getCustomerEmail());
                        return -1;
                    }
                }
            }

             // 4. Insert into Customer table
            String sqlCustomer = StringUtils.QUERY_INSERT_CUSTOMER;
             try (PreparedStatement stmtCust = con.prepareStatement(sqlCustomer)) {
                stmtCust.setString(1, customer.getCustomerName());
                stmtCust.setString(2, customer.getCustomerEmail());
                // Handle potential null DOB
                if (customer.getCustomerDOB() != null) {
                    stmtCust.setDate(3, Date.valueOf(customer.getCustomerDOB()));
                } else {
                    stmtCust.setNull(3, java.sql.Types.DATE);
                }
                stmtCust.setString(4, customer.getCustomerContact());
                stmtCust.setInt(5, passwordId); // Use the generated Password_Id

                int rowsAffected = stmtCust.executeUpdate();
                 if (rowsAffected == 0) {
                     con.rollback();
                     LOGGER.severe("Failed to insert into Customer table for: " + customer.getCustomerEmail());
                     return -1;
                 }
             }

            con.commit(); // Commit transaction
            LOGGER.info("Customer registered successfully: " + customer.getCustomerEmail());
            return 1; // Success

        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error during customer registration transaction", e);
            if (con != null) {
                try {
                    con.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error rolling back transaction", ex);
                }
            }
            return -1; // Server error
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); // Reset auto-commit
                    con.close();
                } catch (SQLException e) {
                     LOGGER.log(Level.SEVERE, "Error closing connection", e);
                }
            }
        }
    }


     // --- Get User Details (Post-Login) ---

    public Object getUserDetailsByEmail(String email, String role) {
        String sql = "";
        if (StringUtils.ROLE_ADMIN.equals(role)) {
            sql = StringUtils.QUERY_GET_ADMIN_BY_EMAIL;
        } else if (StringUtils.ROLE_CUSTOMER.equals(role)) {
            sql = StringUtils.QUERY_GET_CUSTOMER_BY_EMAIL;
        } else {
            return null; // Invalid role
        }

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    if (StringUtils.ROLE_ADMIN.equals(role)) {
                        AdminModel admin = new AdminModel();
                        admin.setAdminId(rs.getInt("Admin_Id"));
                        admin.setAdminName(rs.getString("Admin_Name"));
                        admin.setAdminEmail(rs.getString("Admin_Email"));
                        Date dob = rs.getDate("Admin_DOB");
                        admin.setAdminDOB(dob != null ? dob.toLocalDate() : null);
                        admin.setAdminContact(rs.getString("Admin_Contact"));
                        // passwordId is usually not needed after login, but role is useful
                        return admin;
                    } else { // Customer
                        CustomerModel customer = new CustomerModel();
                        customer.setCustomerId(rs.getInt("Customer_Id"));
                        customer.setCustomerName(rs.getString("Customer_Name"));
                        customer.setCustomerEmail(rs.getString("Customer_Email"));
                        Date dob = rs.getDate("Customer_DOB");
                        customer.setCustomerDOB(dob != null ? dob.toLocalDate() : null);
                        customer.setCustomerContact(rs.getString("Customer_Contact"));
                        return customer;
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error getting user details for email: " + email + " role: " + role, e);
        }
        return null;
    }


    // --- Stock Management (CRUD) ---

    /**
     * Retrieves all stocks from the database.
     * @return A list of StockModel objects, or an empty list if none found/error.
     */
    public List<StockModel> getAllStocks() {
        List<StockModel> stocks = new ArrayList<>();
        String sql = StringUtils.QUERY_GET_ALL_STOCKS;
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stocks.add(mapResultSetToStockModel(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all stocks", e);
        }
        return stocks;
    }

    /**
     * Retrieves only active stocks from the database.
     * @return A list of active StockModel objects.
     */
     public List<StockModel> getActiveStocks() {
        List<StockModel> stocks = new ArrayList<>();
        String sql = StringUtils.QUERY_GET_ACTIVE_STOCKS; // Use specific query for active stocks
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                stocks.add(mapResultSetToStockModel(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving active stocks", e);
        }
        return stocks;
    }


    /**
     * Retrieves a single stock by its ID.
     * @param stockId The ID of the stock to retrieve.
     * @return The StockModel object, or null if not found or error.
     */
    public StockModel getStockById(int stockId) {
        String sql = StringUtils.QUERY_GET_STOCK_BY_ID;
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, stockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStockModel(rs);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving stock by ID: " + stockId, e);
        }
        return null;
    }

    /**
     * Adds a new stock record to the database.
     * @param stock The StockModel object containing the data for the new stock.
     * @return True if the stock was added successfully, false otherwise.
     */
    public boolean addStock(StockModel stock) {
        String sql = StringUtils.QUERY_INSERT_STOCK;
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            mapStockModelToStatement(stock, stmt); // Use helper to set parameters
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error adding stock: " + stock.getSymbol(), e);
            return false;
        }
    }

    /**
     * Updates an existing stock record in the database.
     * @param stock The StockModel object containing the updated data. Must have a valid stockId.
     * @return True if the stock was updated successfully, false otherwise.
     */
    public boolean updateStock(StockModel stock) {
        String sql = StringUtils.QUERY_UPDATE_STOCK;
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            mapStockModelToStatement(stock, stmt); // Reuse helper
            stmt.setInt(11, stock.getStockId()); // Set the WHERE clause parameter

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error updating stock ID: " + stock.getStockId(), e);
            return false;
        }
    }

    /**
     * Deletes a stock record from the database.
     * @param stockId The ID of the stock to delete.
     * @return True if the stock was deleted successfully, false otherwise.
     */
    public boolean deleteStock(int stockId) {
        String sql = StringUtils.QUERY_DELETE_STOCK;
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, stockId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error deleting stock ID: " + stockId, e);
            return false;
        }
    }


    // --- Helper Methods ---

    /**
     * Maps a ResultSet row to a StockModel object.
     */
    private StockModel mapResultSetToStockModel(ResultSet rs) throws SQLException {
        StockModel stock = new StockModel();
        stock.setStockId(rs.getInt("Stock_Id"));
        stock.setSymbol(rs.getString("Symbol"));
        stock.setCompanyName(rs.getString("Company_Name"));
        stock.setInstrumentId(rs.getString("Instrument_Id"));
        stock.setTotalShares(rs.getLong("Total_Shares")); // Use getLong, handle potential null if needed
         if (rs.wasNull()) stock.setTotalShares(null);
        Date listingDate = rs.getDate("Listing_Date");
        stock.setListingDate(listingDate != null ? listingDate.toLocalDate() : null);
        stock.setOpeningPrice(rs.getBigDecimal("Opening_Price"));
        stock.setClosingPrice(rs.getBigDecimal("Closing_Price"));
        stock.setCategory(rs.getString("Category"));
        stock.setExchange(rs.getString("Exchange"));
        stock.setStatus(rs.getString("Status"));
        Timestamp dateAdded = rs.getTimestamp("Date_Added");
        stock.setDateAdded(dateAdded != null ? dateAdded.toLocalDateTime(): null);
        Timestamp lastUpdated = rs.getTimestamp("Last_Updated");
         stock.setLastUpdated(lastUpdated != null ? lastUpdated.toLocalDateTime(): null);
        return stock;
    }

     /**
     * Maps a StockModel object's fields to a PreparedStatement.
     * Order matches INSERT and UPDATE queries (excluding ID for INSERT, excluding ID at end for UPDATE).
     */
     private void mapStockModelToStatement(StockModel stock, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, stock.getSymbol());
        stmt.setString(2, stock.getCompanyName());
        stmt.setString(3, stock.getInstrumentId()); // Handle nulls if necessary

        if (stock.getTotalShares() != null) {
             stmt.setLong(4, stock.getTotalShares());
        } else {
            stmt.setNull(4, java.sql.Types.BIGINT);
        }

        if (stock.getListingDate() != null) {
            stmt.setDate(5, Date.valueOf(stock.getListingDate()));
        } else {
             stmt.setNull(5, java.sql.Types.DATE);
        }

        stmt.setBigDecimal(6, stock.getOpeningPrice()); // setBigDecimal handles null
        stmt.setBigDecimal(7, stock.getClosingPrice()); // setBigDecimal handles null
        stmt.setString(8, stock.getCategory());
        stmt.setString(9, stock.getExchange());
        stmt.setString(10, stock.getStatus());
        // Note: stockId is set separately for UPDATE in the calling method
    }
}