package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.stockx.database.DBController;
import com.stockx.model.StockModel;
import com.stockx.util.StringUtils;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

@WebServlet(StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD)
public class CustomerDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CustomerDashboardController.class.getName());
    private DBController dbController;

     @Override
    public void init() throws ServletException {
        super.init();
        this.dbController = new DBController();
    }


    /**
     * Displays the customer dashboard with *active* stocks.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.fine("Customer Dashboard doGet called.");

        // Fetch only active stocks for customers
        List<StockModel> stockList = dbController.getActiveStocks();

         if (stockList == null) {
            LOGGER.severe("Failed to retrieve active stock list for customer dashboard.");
            request.setAttribute(StringUtils.MESSAGE_ERROR, "Could not load stock data. Please try again later.");
        }

        // Set the list as a request attribute
        request.setAttribute(StringUtils.REQ_ATTRIBUTE_STOCKS, stockList);

        // Forward to the JSP page
        request.getRequestDispatcher(StringUtils.PAGE_URL_CUSTOMER_DASHBOARD).forward(request, response);
    }

    // POST not typically needed for a read-only customer dashboard
    // protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //     doGet(request, response);
    // }
}