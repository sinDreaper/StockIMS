package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import com.stockx.service.RegisterService;
import com.stockx.util.StringUtils;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

@WebServlet(StringUtils.SERVLET_URL_REGISTER)
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());
    private RegisterService registerService;

     @Override
    public void init() throws ServletException {
        super.init();
        this.registerService = new RegisterService();
    }

    /**
     * Displays the registration page.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(StringUtils.PAGE_URL_REGISTER).forward(request, response);
    }

    /**
     * Handles the customer registration form submission.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Extract parameters
        String name = request.getParameter(StringUtils.USER_NAME);
        String email = request.getParameter(StringUtils.EMAIL);
        String password = request.getParameter(StringUtils.PASSWORD);
        String retypePassword = request.getParameter(StringUtils.RETYPE_PASSWORD);
        String dob = request.getParameter(StringUtils.DOB); // Optional
        String contact = request.getParameter(StringUtils.CONTACT_NUMBER); // Optional

        // Call service to register
        String resultMessage = registerService.registerCustomer(name, email, password, retypePassword, dob, contact);

        // Handle result
        if (StringUtils.MESSAGE_SUCCESS_REGISTER.equals(resultMessage)) {
            LOGGER.info("Registration successful for: " + email);
            // Redirect to login page with success message (using query parameter)
            response.sendRedirect(request.getContextPath() + StringUtils.SERVLET_URL_LOGIN + "?success=" + StringUtils.TRUE);
        } else {
            LOGGER.warning("Registration failed for: " + email + " Reason: " + resultMessage);
            // Forward back to registration page with error message and retain form data
            request.setAttribute(StringUtils.MESSAGE_ERROR, resultMessage);
            request.setAttribute(StringUtils.USER_NAME, name);
            request.setAttribute(StringUtils.EMAIL, email);
            request.setAttribute(StringUtils.DOB, dob);
            request.setAttribute(StringUtils.CONTACT_NUMBER, contact);
            request.getRequestDispatcher(StringUtils.PAGE_URL_REGISTER).forward(request, response);
        }
    }
}