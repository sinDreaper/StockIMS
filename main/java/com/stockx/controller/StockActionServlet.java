package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.stockx.database.DBController;
import com.stockx.model.StockModel;
import com.stockx.util.StringUtils;
import com.stockx.util.ValidationUtil;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

@WebServlet(StringUtils.SERVLET_URL_STOCK_ACTION)
public class StockActionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(StockActionServlet.class.getName());
    private DBController dbController;

    @Override
    public void init() throws ServletException {
        super.init();
        this.dbController = new DBController();
    }

    // GET request might be used to fetch a stock for editing, but usually done via AJAX or hidden fields.
    // For simplicity, we focus on POST for Add/Update/Delete.
    // protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //     // Handle GET if needed, e.g., redirect if accessed directly
    //     response.sendRedirect(request.getContextPath() + StringUtils.SERVLET_URL_ADMIN_DASHBOARD);
    // }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Security Check: Ensure user is logged in and is an Admin
        if (session == null || !StringUtils.ROLE_ADMIN.equals(session.getAttribute(StringUtils.SESSION_USER_ROLE))) {
             LOGGER.warning("Unauthorized attempt to access StockActionServlet.");
             response.sendError(HttpServletResponse.SC_FORBIDDEN, StringUtils.MESSAGE_ERROR_NOT_AUTHORIZED);
            return;
        }

        String action = request.getParameter(StringUtils.ACTION);
        if (action == null) {
             LOGGER.warning("StockActionServlet called without an action parameter.");
             setErrorAndRedirect(request, response, "Invalid request.");
            return;
        }

        try {
            switch (action) {
                case StringUtils.ACTION_ADD:
                    handleAddStock(request, response);
                    break;
                case StringUtils.ACTION_UPDATE:
                    handleUpdateStock(request, response);
                    break;
                case StringUtils.ACTION_DELETE:
                    handleDeleteStock(request, response);
                    break;
                default:
                     LOGGER.warning("StockActionServlet called with unknown action: " + action);
                     setErrorAndRedirect(request, response, "Unknown action specified.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error processing stock action: " + action, e);
             setErrorAndRedirect(request, response, StringUtils.MESSAGE_ERROR_SERVER);
        }
    }

    private void handleAddStock(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StockModel stock = mapRequestToStockModel(request);
        String validationError = validateStock(stock, true); // Validate for add

        if (validationError != null) {
             setErrorAndRedirect(request, response, validationError);
             return;
        }

        boolean success = dbController.addStock(stock);
        if (success) {
            LOGGER.info("Stock added successfully: " + stock.getSymbol());
             setSuccessAndRedirect(request, response, StringUtils.MESSAGE_SUCCESS_ADD);
        } else {
            LOGGER.severe("Failed to add stock: " + stock.getSymbol());
             setErrorAndRedirect(request, response, StringUtils.MESSAGE_ERROR_ADD);
        }
    }

     private void handleUpdateStock(HttpServletRequest request, HttpServletResponse response) throws IOException {
         String stockIdStr = request.getParameter(StringUtils.STOCK_ID);
         if (ValidationUtil.isNullOrEmpty(stockIdStr) || !ValidationUtil.isNumbersOnly(stockIdStr)) {
              setErrorAndRedirect(request, response, "Invalid or missing Stock ID for update.");
             return;
         }
         int stockId = Integer.parseInt(stockIdStr);

         StockModel stock = mapRequestToStockModel(request);
         stock.setStockId(stockId); // Set the ID for update
         String validationError = validateStock(stock, false); // Validate for update

         if (validationError != null) {
              setErrorAndRedirect(request, response, validationError);
              return;
         }

         boolean success = dbController.updateStock(stock);
         if (success) {
             LOGGER.info("Stock updated successfully: ID " + stockId);
              setSuccessAndRedirect(request, response, StringUtils.MESSAGE_SUCCESS_UPDATE);
         } else {
              LOGGER.severe("Failed to update stock: ID " + stockId);
              setErrorAndRedirect(request, response, StringUtils.MESSAGE_ERROR_UPDATE);
         }
     }

    private void handleDeleteStock(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String stockIdStr = request.getParameter(StringUtils.STOCK_ID);
         if (ValidationUtil.isNullOrEmpty(stockIdStr) || !ValidationUtil.isNumbersOnly(stockIdStr)) {
             setErrorAndRedirect(request, response, "Invalid or missing Stock ID for deletion.");
            return;
        }
        int stockId = Integer.parseInt(stockIdStr);

        boolean success = dbController.deleteStock(stockId);
        if (success) {
             LOGGER.info("Stock deleted successfully: ID " + stockId);
             setSuccessAndRedirect(request, response, StringUtils.MESSAGE_SUCCESS_DELETE);
        } else {
             LOGGER.severe("Failed to delete stock: ID " + stockId);
            setErrorAndRedirect(request, response, StringUtils.MESSAGE_ERROR_DELETE);
        }
    }

     private StockModel mapRequestToStockModel(HttpServletRequest request) {
        StockModel stock = new StockModel();
        stock.setSymbol(request.getParameter(StringUtils.STOCK_SYMBOL));
        stock.setCompanyName(request.getParameter(StringUtils.STOCK_COMPANY_NAME));
        stock.setInstrumentId(request.getParameter(StringUtils.STOCK_INSTRUMENT_ID)); // Optional
        stock.setCategory(request.getParameter(StringUtils.STOCK_CATEGORY));
        stock.setExchange(request.getParameter(StringUtils.STOCK_EXCHANGE));
        stock.setStatus(request.getParameter(StringUtils.STOCK_STATUS));

        // Handle numeric and date fields carefully
         String totalSharesStr = request.getParameter(StringUtils.STOCK_TOTAL_SHARES);
        if (!ValidationUtil.isNullOrEmpty(totalSharesStr) && ValidationUtil.isValidLong(totalSharesStr)) {
            stock.setTotalShares(Long.parseLong(totalSharesStr));
        }

        String listingDateStr = request.getParameter(StringUtils.STOCK_LISTING_DATE);
        if (!ValidationUtil.isNullOrEmpty(listingDateStr)) {
            try {
                stock.setListingDate(LocalDate.parse(listingDateStr)); // Assumes YYYY-MM-DD
            } catch (DateTimeParseException e) {
                // Ignore invalid date format during mapping, validation will catch it
            }
        }

         String openingPriceStr = request.getParameter(StringUtils.STOCK_OPENING_PRICE);
          stock.setOpeningPriceFromString(openingPriceStr); // Use safe setter


         String closingPriceStr = request.getParameter(StringUtils.STOCK_CLOSING_PRICE);
          stock.setClosingPriceFromString(closingPriceStr); // Use safe setter


        return stock;
    }

     private String validateStock(StockModel stock, boolean isAdd) {
         if (ValidationUtil.isNullOrEmpty(stock.getSymbol())) return "Symbol cannot be empty.";
         if (ValidationUtil.isNullOrEmpty(stock.getCompanyName())) return "Company Name cannot be empty.";
         if (ValidationUtil.isNullOrEmpty(stock.getStatus()) || (!"Active".equals(stock.getStatus()) && !"Inactive".equals(stock.getStatus()))) return "Invalid status selected.";

         // Add more specific validations as needed (length, format, range)
         // Example: Check if prices are valid numbers if provided
          String openingPriceStr = (stock.getOpeningPrice() != null) ? stock.getOpeningPrice().toPlainString() : null;
          if (!ValidationUtil.isNullOrEmpty(openingPriceStr) && !ValidationUtil.isValidDecimal(openingPriceStr)) return "Opening Price must be a valid number.";

          String closingPriceStr = (stock.getClosingPrice() != null) ? stock.getClosingPrice().toPlainString() : null;
           if (!ValidationUtil.isNullOrEmpty(closingPriceStr) && !ValidationUtil.isValidDecimal(closingPriceStr)) return "Closing Price must be a valid number.";


         // For 'add', you might check if symbol already exists (optional, DB constraint handles it too)

         return null; // No errors found
     }

     // Helper to set session message and redirect
    private void setSuccessAndRedirect(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
         HttpSession session = request.getSession();
         session.setAttribute(StringUtils.MESSAGE_SUCCESS, message); // Use session for messages on redirect
         response.sendRedirect(request.getContextPath() + StringUtils.SERVLET_URL_ADMIN_DASHBOARD);
     }

     // Helper to set session message and redirect
     private void setErrorAndRedirect(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
          HttpSession session = request.getSession();
          session.setAttribute(StringUtils.MESSAGE_ERROR, message); // Use session for messages on redirect
          response.sendRedirect(request.getContextPath() + StringUtils.SERVLET_URL_ADMIN_DASHBOARD);
      }

}