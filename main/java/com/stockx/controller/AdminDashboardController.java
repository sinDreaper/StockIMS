package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.stockx.database.DBController; // Use DBController directly for simple reads
import com.stockx.model.StockModel;
import com.stockx.util.StringUtils;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

@WebServlet(StringUtils.SERVLET_URL_ADMIN_DASHBOARD)
public class AdminDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminDashboardController.class.getName());
    private DBController dbController;

     @Override
    public void init() throws ServletException {
        super.init();
        this.dbController = new DBController();
    }

    /**
     * Displays the admin dashboard with the list of all stocks.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.fine("Admin Dashboard doGet called.");

        // Fetch all stocks from the database
        List<StockModel> stockList = dbController.getAllStocks();

        if (stockList == null) {
            // Handle potential DB error during fetch
             LOGGER.severe("Failed to retrieve stock list for admin dashboard.");
            request.setAttribute(StringUtils.MESSAGE_ERROR, "Could not load stock data. Please try again later.");
            // Optionally redirect to an error page or show message on dashboard
        }

        // Set the list as a request attribute
        request.setAttribute(StringUtils.REQ_ATTRIBUTE_STOCKS, stockList);

        // Forward to the JSP page
        request.getRequestDispatcher(StringUtils.PAGE_URL_ADMIN_DASHBOARD).forward(request, response);
    }

    /**
     * POST is typically handled by a dedicated action servlet (StockActionServlet)
     * for Add/Update/Delete to keep this controller focused on displaying the dashboard.
     */
    // protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //     // Delegate to doGet or handle specific POST actions if necessary (not recommended here)
    //     doGet(request, response);
    // }
}