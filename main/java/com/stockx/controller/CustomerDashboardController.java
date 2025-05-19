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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOGGER.fine("Customer Dashboard doGet called.");

        String viewParam = request.getParameter("view");
        List<StockModel> stockList;

        if ("stocks".equals(viewParam)) {
            String sortBy = request.getParameter("sort_by");
            String sortOrder = request.getParameter("sort_order");
            String filterCategory = request.getParameter("filter_category");

            LOGGER.fine("Fetching all stocks with sort: " + sortBy + " " + sortOrder + ", filterCategory: " + filterCategory);
            stockList = dbController.getAllStocksFilteredAndSorted(sortBy, sortOrder, filterCategory, null);
        } else {
            LOGGER.fine("Fetching active stocks for default dashboard view.");
            stockList = dbController.getActiveStocks();
        }

        if (stockList == null) {
            LOGGER.severe("Failed to retrieve stock list. View: " + (viewParam != null ? viewParam : "default"));
            request.setAttribute(StringUtils.MESSAGE_ERROR, "Could not load stock data. Please try again later.");
        }

        request.setAttribute(StringUtils.REQ_ATTRIBUTE_STOCKS, stockList);
        request.getRequestDispatcher(StringUtils.PAGE_URL_CUSTOMER_DASHBOARD).forward(request, response);
    }
}
