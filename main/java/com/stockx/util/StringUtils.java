package com.stockx.util;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

public class StringUtils {

    // Start: DB Connection
    public static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String LOCALHOST_URL = "jdbc:mysql://localhost:3316/stockx_db"; 
    public static final String LOCALHOST_USERNAME = "root"; 
    public static final String LOCALHOST_PASSWORD = ""; 
    // End: DB Connection

    // Start: SQL Queries
    // Password Table
    public static final String QUERY_INSERT_PASSWORD = "INSERT INTO Password (Email, Password_Hash, User_Role) VALUES (?, ?, ?)";
    public static final String QUERY_GET_PASSWORD_INFO_BY_EMAIL = "SELECT Password_Id, Password_Hash, User_Role FROM Password WHERE Email = ?";
    public static final String QUERY_CHECK_EMAIL_EXISTS = "SELECT COUNT(*) FROM Password WHERE Email = ?";

    // Customer Table
    public static final String QUERY_INSERT_CUSTOMER = "INSERT INTO Customer (Customer_Name, Customer_Email, Customer_DOB, Customer_Contact, Password_Id) VALUES (?, ?, ?, ?, ?)";
    public static final String QUERY_GET_CUSTOMER_BY_ID = "SELECT c.Customer_Id, c.Customer_Name, c.Customer_Email, c.Customer_DOB, c.Customer_Contact, p.User_Role FROM Customer c JOIN Password p ON c.Password_Id = p.Password_Id WHERE c.Customer_Id = ?";
    public static final String QUERY_GET_CUSTOMER_BY_EMAIL = "SELECT c.Customer_Id, c.Customer_Name, c.Customer_Email, c.Customer_DOB, c.Customer_Contact, p.User_Role FROM Customer c JOIN Password p ON c.Password_Id = p.Password_Id WHERE c.Customer_Email = ?";


    // Admin Table
    public static final String QUERY_GET_ADMIN_BY_ID = "SELECT a.Admin_Id, a.Admin_Name, a.Admin_Email, a.Admin_DOB, a.Admin_Contact, p.User_Role FROM Admin a JOIN Password p ON a.Password_Id = p.Password_Id WHERE a.Admin_Id = ?";
    public static final String QUERY_GET_ADMIN_BY_EMAIL = "SELECT a.Admin_Id, a.Admin_Name, a.Admin_Email, a.Admin_DOB, a.Admin_Contact, p.User_Role FROM Admin a JOIN Password p ON a.Password_Id = p.Password_Id WHERE a.Admin_Email = ?";


    // Stock Table
    public static final String QUERY_INSERT_STOCK = "INSERT INTO Stock (Symbol, Company_Name, Instrument_Id, Total_Shares, Listing_Date, Opening_Price, Closing_Price, Category, Exchange, Status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String QUERY_GET_ALL_STOCKS = "SELECT Stock_Id, Symbol, Company_Name, Instrument_Id, Total_Shares, Listing_Date, Opening_Price, Closing_Price, Category, Exchange, Status, Date_Added, Last_Updated FROM Stock ORDER BY Company_Name ASC";
    public static final String QUERY_GET_STOCK_BY_ID = "SELECT Stock_Id, Symbol, Company_Name, Instrument_Id, Total_Shares, Listing_Date, Opening_Price, Closing_Price, Category, Exchange, Status, Date_Added, Last_Updated FROM Stock WHERE Stock_Id = ?";
     public static final String QUERY_GET_ACTIVE_STOCKS = "SELECT Stock_Id, Symbol, Company_Name, Instrument_Id, Total_Shares, Listing_Date, Opening_Price, Closing_Price, Category, Exchange, Status, Date_Added, Last_Updated FROM Stock WHERE Status = 'Active' ORDER BY Company_Name ASC";
    public static final String QUERY_UPDATE_STOCK = "UPDATE Stock SET Symbol = ?, Company_Name = ?, Instrument_Id = ?, Total_Shares = ?, Listing_Date = ?, Opening_Price = ?, Closing_Price = ?, Category = ?, Exchange = ?, Status = ? WHERE Stock_Id = ?";
    public static final String QUERY_DELETE_STOCK = "DELETE FROM Stock WHERE Stock_Id = ?";
    // End: SQL Queries

    // Start: Parameter names (Login/Register/User/Stock)
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String RETYPE_PASSWORD = "retypePassword";
    public static final String USER_NAME = "name"; // Changed from firstName/lastName
    public static final String DOB = "dob";
    public static final String CONTACT_NUMBER = "contactNumber";
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_CUSTOMER = "Customer";

    public static final String STOCK_ID = "stockId";
    public static final String STOCK_SYMBOL = "symbol";
    public static final String STOCK_COMPANY_NAME = "companyName";
    public static final String STOCK_INSTRUMENT_ID = "instrumentId";
    public static final String STOCK_TOTAL_SHARES = "totalShares";
    public static final String STOCK_LISTING_DATE = "listingDate";
    public static final String STOCK_OPENING_PRICE = "openingPrice";
    public static final String STOCK_CLOSING_PRICE = "closingPrice";
    public static final String STOCK_CATEGORY = "category";
    public static final String STOCK_EXCHANGE = "exchange";
    public static final String STOCK_STATUS = "status";
    public static final String ACTION = "action"; // For CRUD operations in admin forms
    public static final String ACTION_ADD = "add";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";
    // End: Parameter names

    // Start: Validation Messages
    public static final String MESSAGE_SUCCESS_REGISTER = "Registration Successful! Please login.";
    public static final String MESSAGE_ERROR_REGISTER = "Registration Failed. Please correct the form data.";
    public static final String MESSAGE_ERROR_EMAIL_EXISTS = "Email is already registered.";
    public static final String MESSAGE_ERROR_PASSWORD_UNMATCHED = "Passwords do not match.";
    public static final String MESSAGE_ERROR_INVALID_DATA = "Invalid data entered. Please check your inputs.";

    public static final String MESSAGE_SUCCESS_LOGIN = "Login Successful!";
    public static final String MESSAGE_ERROR_LOGIN = "Invalid email or password.";
    public static final String MESSAGE_ERROR_ACCOUNT_NOT_FOUND = "Account not found for this email."; // Changed message

    public static final String MESSAGE_ERROR_SERVER = "An unexpected server error occurred. Please try again later.";
    public static final String MESSAGE_SUCCESS_DELETE = "Successfully Deleted!";
    public static final String MESSAGE_ERROR_DELETE = "Cannot delete the item!";
    public static final String MESSAGE_SUCCESS_ADD = "Successfully Added!";
    public static final String MESSAGE_ERROR_ADD = "Failed to add item!";
     public static final String MESSAGE_SUCCESS_UPDATE = "Successfully Updated!";
    public static final String MESSAGE_ERROR_UPDATE = "Failed to update item!";
    public static final String MESSAGE_ERROR_NOT_AUTHORIZED = "You are not authorized to perform this action.";


    public static final String MESSAGE_SUCCESS = "successMessage";
    public static final String MESSAGE_ERROR = "errorMessage";
    // End: Validation Messages

    // Start: JSP Route
    public static final String PAGE_URL_LOGIN = "/WEB-INF/pages/login.jsp";
    public static final String PAGE_URL_LANDING = "/WEB-INF/pages/landing.jsp";
    public static final String PAGE_URL_REGISTER = "/WEB-INF/pages/register.jsp";
    public static final String PAGE_URL_HOME = "/WEB-INF/pages/home.jsp"; // Customer landing
    public static final String PAGE_URL_ADMIN_DASHBOARD = "/WEB-INF/pages/admin_dashboard.jsp";
    public static final String PAGE_URL_CUSTOMER_DASHBOARD = "/WEB-INF/pages/customer_dashboard.jsp"; // Could be same as home
    public static final String PAGE_URL_USER_PROFILE = "/WEB-INF/pages/user_profile.jsp";
    public static final String PAGE_URL_ABOUT_US = "/WEB-INF/pages/about_us.jsp";
    public static final String PAGE_URL_CONTACT_US = "/WEB-INF/pages/contact_us.jsp";
    public static final String PAGE_URL_HEADER = "/WEB-INF/pages/header.jsp";
    public static final String PAGE_URL_FOOTER = "/WEB-INF/pages/footer.jsp";
    public static final String PAGE_URL_ERROR = "/WEB-INF/pages/error.jsp"; // Optional error page
    // End: JSP Route

    // Start: Servlet Route
    public static final String SERVLET_URL_LANDING = "/landing";
    public static final String SERVLET_URL_LOGIN = "/login";
    public static final String SERVLET_URL_REGISTER = "/register";
    public static final String SERVLET_URL_LOGOUT = "/logout";
    public static final String SERVLET_URL_HOME = "/home"; // Might redirect based on role
    public static final String SERVLET_URL_ADMIN_DASHBOARD = "/admin_dashboard";
    public static final String SERVLET_URL_CUSTOMER_DASHBOARD = "/customer_dashboard";
    public static final String SERVLET_URL_USER_PROFILE = "/user_profile";
    public static final String SERVLET_URL_ABOUT_US = "/aboutus";
    public static final String SERVLET_URL_CONTACT_US = "/contactus";
    public static final String SERVLET_URL_STOCK_ACTION = "/admin/stock"; // Servlet for admin stock CRUD
    // End: Servlet Route

    // Start: Session Attributes / Other Constants
    public static final String SESSION_USER_ID = "user_id";
    public static final String SESSION_USER_NAME = "user_name";
    public static final String SESSION_USER_EMAIL = "user_email";
    public static final String SESSION_USER_ROLE = "user_role"; // Stores "Admin" or "Customer"

    public static final String REQ_ATTRIBUTE_USER = "user"; // For profile page
    public static final String REQ_ATTRIBUTE_STOCKS = "stocks"; // For dashboards

    public static final String TRUE = "true";
    public static final String JSESSIONID = "JSESSIONID";
    public static final String LOGIN = "Login";
    public static final String LOGOUT = "Logout";
    public static final String SIGNUP = "Sign Up";
    public static final String USER_PROFILE = "Profile";
    // End: Session Attributes / Other Constants
}