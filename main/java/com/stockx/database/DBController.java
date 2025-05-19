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
import java.util.Set; // Added for VALID_SORT_COLUMNS
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

    // Constants for sorting/filtering to be used internally or passed from service
    public static final String DEFAULT_SORT_COLUMN = "Company_Name"; // Default database column name
    public static final String DEFAULT_SORT_ORDER = "ASC";

    // Whitelist of valid parameter values that map to database column names for sorting
    // This helps prevent SQL injection.
    // The keys are what you might receive as 'sort_by' parameter from JSP/Controller.
    // The values (if different, or if direct mapping) would be actual DB column names.
    // For simplicity, if param value matches DB column, direct use is fine after validation.
    private static final Set<String> VALID_SORT_PARAM_VALUES = Set.of(
        "Symbol", "CompanyName", "CurrentPrice", "Exchange", "Category", "ListingDate", "Status", "Date_Added"
    );


    private Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName(StringUtils.DRIVER_NAME);
        return DriverManager.getConnection(StringUtils.LOCALHOST_URL, StringUtils.LOCALHOST_USERNAME, StringUtils.LOCALHOST_PASSWORD);
    }

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
        return false;
    }

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
                    passModel.setEmail(email);
                    return passModel;
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error getting password info for email: " + email, e);
        }
        return null;
    }

    public int registerCustomer(CustomerModel customer, String plainPassword) {
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);

            if (checkEmailExists(customer.getCustomerEmail())) {
                 con.rollback();
                 LOGGER.warning("Registration attempt failed: Email already exists - " + customer.getCustomerEmail());
                 return 0;
            }

            String hashedPassword = PasswordEncryptionWithAes.encrypt(plainPassword, customer.getCustomerEmail());
            if (hashedPassword == null) {
                con.rollback();
                LOGGER.severe("Password encryption failed for: " + customer.getCustomerEmail());
                return -1;
            }

            int passwordId = -1;
            String sqlPassword = StringUtils.QUERY_INSERT_PASSWORD;
            try (PreparedStatement stmtPass = con.prepareStatement(sqlPassword, Statement.RETURN_GENERATED_KEYS)) {
                stmtPass.setString(1, customer.getCustomerEmail());
                stmtPass.setString(2, hashedPassword);
                stmtPass.setString(3, StringUtils.ROLE_CUSTOMER);
                int rowsAffected = stmtPass.executeUpdate();
                if (rowsAffected == 0) {
                    con.rollback();
                    LOGGER.severe("Failed to insert into Password table for: " + customer.getCustomerEmail());
                    return -1;
                }
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

            String sqlCustomer = StringUtils.QUERY_INSERT_CUSTOMER;
             try (PreparedStatement stmtCust = con.prepareStatement(sqlCustomer)) {
                stmtCust.setString(1, customer.getCustomerName());
                stmtCust.setString(2, customer.getCustomerEmail());
                if (customer.getCustomerDOB() != null) {
                    stmtCust.setDate(3, Date.valueOf(customer.getCustomerDOB()));
                } else {
                    stmtCust.setNull(3, java.sql.Types.DATE);
                }
                stmtCust.setString(4, customer.getCustomerContact());
                stmtCust.setInt(5, passwordId);
                int rowsAffected = stmtCust.executeUpdate();
                 if (rowsAffected == 0) {
                     con.rollback();
                     LOGGER.severe("Failed to insert into Customer table for: " + customer.getCustomerEmail());
                     return -1;
                 }
             }
            con.commit();
            LOGGER.info("Customer registered successfully: " + customer.getCustomerEmail());
            return 1;
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error during customer registration transaction", e);
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { LOGGER.log(Level.SEVERE, "Error rolling back transaction", ex); }
            }
            return -1;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { LOGGER.log(Level.SEVERE, "Error closing connection", e); }
            }
        }
    }

    public Object getUserDetailsByEmail(String email, String role) {
        String sql = "";
        if (StringUtils.ROLE_ADMIN.equals(role)) {
            sql = StringUtils.QUERY_GET_ADMIN_BY_EMAIL;
        } else if (StringUtils.ROLE_CUSTOMER.equals(role)) {
            sql = StringUtils.QUERY_GET_CUSTOMER_BY_EMAIL;
        } else {
            return null;
        }
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
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
                        return admin;
                    } else {
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

    /**
     * Retrieves a list of stocks, with optional filtering by category and status, and sorting.
     * This is the main method used by controllers needing filtered/sorted stock lists.
     *
     * @param sortByParam Parameter value for sorting column (e.g., "CompanyName", "CurrentPrice").
     * @param sortOrderParam "ASC" or "DESC".
     * @param filterCategory Category to filter by (can be null or empty for no category filter).
     * @param filterStatus Status to filter by (e.g., "Active", "Inactive"; can be null or empty).
     * @return List of StockModel objects.
     */
    public List<StockModel> getAllStocksFilteredAndSorted(String sortByParam, String sortOrderParam, String filterCategory, String filterStatus) {
        List<StockModel> stocks = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(StringUtils.QUERY_GET_ALL_STOCKS_BASE); // Base query: "SELECT ... FROM Stock"

        List<Object> queryParams = new ArrayList<>(); // Parameters for PreparedStatement
        boolean hasWhere = false;

        // --- Apply Filters ---
        if (filterStatus != null && !filterStatus.isEmpty()) {
            sqlBuilder.append(" WHERE Status = ?");
            queryParams.add(filterStatus);
            hasWhere = true;
        }

        if (filterCategory != null && !filterCategory.isEmpty()) {
            sqlBuilder.append(hasWhere ? " AND" : " WHERE").append(" Category = ?");
            queryParams.add(filterCategory);
            // hasWhere = true; // Not strictly needed to update if it's the last filter condition
        }

        // --- Apply Sorting ---
        String dbSortColumn = DEFAULT_SORT_COLUMN; // Default database column to sort by
        if (sortByParam != null && VALID_SORT_PARAM_VALUES.contains(sortByParam)) {
            // Map parameter value to actual database column name if necessary
            if ("CurrentPrice".equalsIgnoreCase(sortByParam)) {
                dbSortColumn = "COALESCE(Closing_Price, Opening_Price)"; // Sort by Closing_Price, fallback to Opening_Price
            } else if ("ListingDate".equalsIgnoreCase(sortByParam)){
                dbSortColumn = "Listing_Date"; // Actual column name
            } else {
                dbSortColumn = sortByParam; // Assumes param value matches DB column name and is whitelisted
            }
        }

        String dbSortOrder = DEFAULT_SORT_ORDER;
        if (sortOrderParam != null && ("ASC".equalsIgnoreCase(sortOrderParam) || "DESC".equalsIgnoreCase(sortOrderParam))) {
            dbSortOrder = sortOrderParam.toUpperCase();
        }

        sqlBuilder.append(" ORDER BY ").append(dbSortColumn).append(" ").append(dbSortOrder);

        // Add secondary sort by default column if primary sort is different and not already by a unique enough field
        if (!dbSortColumn.equalsIgnoreCase(DEFAULT_SORT_COLUMN) && !dbSortColumn.contains("COALESCE")) { // Avoid complex secondary sort if primary is already complex
             sqlBuilder.append(", ").append(DEFAULT_SORT_COLUMN).append(" ").append(DEFAULT_SORT_ORDER);
        }


        LOGGER.info("Executing DB query for stocks: " + sqlBuilder.toString() + " with params: " + queryParams);

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlBuilder.toString())) {

            // Set PreparedStatement parameters
            for (int i = 0; i < queryParams.size(); i++) {
                stmt.setObject(i + 1, queryParams.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                int rowNum = 0;
                while (rs.next()) {
                    rowNum++;
                    try {
                        stocks.add(mapResultSetToStockModel(rs));
                    } catch (SQLException e) { // Catch SQLException specifically from mapping
                        LOGGER.log(Level.SEVERE, "Error mapping stock at row: " + rowNum + ". Stock_Id: " + rs.getInt("Stock_Id") + ". Skipping this stock.", e);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving filtered/sorted stocks from database.", e);
        }
        LOGGER.info("getAllStocksFilteredAndSorted retrieved " + stocks.size() + " stocks.");
        return stocks;
    }


    /**
     * Convenience method for Admin Dashboard to get all stocks with status filter and sorting.
     * Assumes admin does not filter by category from their dashboard UI directly.
     */
    public List<StockModel> getAllStocks(String sortBy, String sortOrder, String filterStatus) {
        return getAllStocksFilteredAndSorted(sortBy, sortOrder, null, filterStatus);
    }

    /**
     * Original method to get all stocks without any filters or specific sorting from parameters.
     * Uses default sorting.
     */
    public List<StockModel> getAllStocks() {
        return getAllStocksFilteredAndSorted(null, null, null, null);
    }


    /**
     * Retrieves only active stocks from the database, ordered by default.
     * Used for the default customer dashboard summary.
     */
     public List<StockModel> getActiveStocks() {
        List<StockModel> stocks = new ArrayList<>();
        // Using the more generic method with a specific filter for status
        return getAllStocksFilteredAndSorted(null, null, null, "Active");
    }


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

    public boolean addStock(StockModel stock) {
        String sql = StringUtils.QUERY_INSERT_STOCK;
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            mapStockModelToStatement(stock, stmt);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error adding stock: " + stock.getSymbol(), e);
            return false;
        }
    }

    public boolean updateStock(StockModel stock) {
        String sql = StringUtils.QUERY_UPDATE_STOCK;
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            mapStockModelToStatement(stock, stmt);
            stmt.setInt(11, stock.getStockId()); // WHERE Stock_Id = ?
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error updating stock ID: " + stock.getStockId(), e);
            return false;
        }
    }

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

    private StockModel mapResultSetToStockModel(ResultSet rs) throws SQLException {
        StockModel stock = new StockModel();
        stock.setStockId(rs.getInt("Stock_Id"));
        stock.setSymbol(rs.getString("Symbol"));
        stock.setCompanyName(rs.getString("Company_Name"));
        stock.setInstrumentId(rs.getString("Instrument_Id"));
        stock.setTotalShares(rs.getLong("Total_Shares"));
         if (rs.wasNull()) stock.setTotalShares(null);
        Date listingDateRaw = rs.getDate("Listing_Date"); // Changed variable name
        stock.setListingDate(listingDateRaw != null ? listingDateRaw.toLocalDate() : null);
        stock.setOpeningPrice(rs.getBigDecimal("Opening_Price"));
        stock.setClosingPrice(rs.getBigDecimal("Closing_Price"));
        stock.setCategory(rs.getString("Category"));
        stock.setExchange(rs.getString("Exchange"));
        stock.setStatus(rs.getString("Status"));
        Timestamp dateAddedRaw = rs.getTimestamp("Date_Added"); // Changed variable name
        stock.setDateAdded(dateAddedRaw != null ? dateAddedRaw.toLocalDateTime(): null);
        Timestamp lastUpdatedRaw = rs.getTimestamp("Last_Updated"); // Changed variable name
         stock.setLastUpdated(lastUpdatedRaw != null ? lastUpdatedRaw.toLocalDateTime(): null);
        return stock;
    }

     private void mapStockModelToStatement(StockModel stock, PreparedStatement stmt) throws SQLException {
        stmt.setString(1, stock.getSymbol());
        stmt.setString(2, stock.getCompanyName());
        stmt.setString(3, stock.getInstrumentId());
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
        stmt.setBigDecimal(6, stock.getOpeningPrice());
        stmt.setBigDecimal(7, stock.getClosingPrice());
        stmt.setString(8, stock.getCategory());
        stmt.setString(9, stock.getExchange());
        stmt.setString(10, stock.getStatus());
    }
}