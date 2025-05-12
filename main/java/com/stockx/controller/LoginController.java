package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.stockx.model.AdminModel;
import com.stockx.model.CustomerModel;
import com.stockx.model.PasswordModel;
import com.stockx.service.LoginService;
import com.stockx.util.StringUtils;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

@WebServlet(StringUtils.SERVLET_URL_LOGIN)
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    private LoginService loginService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.loginService = new LoginService(); // Initialize service
    }

    /**
     * Handles GET requests to display the login page.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is already logged in, redirect if necessary
         HttpSession session = request.getSession(false);
         if (session != null && session.getAttribute(StringUtils.SESSION_USER_EMAIL) != null) {
             String role = (String) session.getAttribute(StringUtils.SESSION_USER_ROLE);
             redirectToDashboard(response, request.getContextPath(), role);
             return;
         }
        request.getRequestDispatcher(StringUtils.PAGE_URL_LOGIN).forward(request, response);
    }

    /**
     * Handles POST requests for user login authentication.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter(StringUtils.EMAIL);
        String password = request.getParameter(StringUtils.PASSWORD);
        String contextPath = request.getContextPath();

        PasswordModel authResult = loginService.authenticateUser(email, password);

        if (authResult != null) {
            // Authentication successful
            LOGGER.info("Login successful for: " + email);

             // Fetch full user details
            Object userDetails = loginService.getUserDetails(email, authResult.getUserRole());

            if(userDetails == null){
                 LOGGER.log(Level.SEVERE, "Login succeeded but failed to fetch user details for: " + email);
                 request.setAttribute(StringUtils.MESSAGE_ERROR, StringUtils.MESSAGE_ERROR_SERVER);
                 request.getRequestDispatcher(StringUtils.PAGE_URL_LOGIN).forward(request, response);
                 return;
            }

            // Create session and store user info
            HttpSession session = request.getSession(true); // Create new session
            session.setAttribute(StringUtils.SESSION_USER_EMAIL, email);
            session.setAttribute(StringUtils.SESSION_USER_ROLE, authResult.getUserRole());

            // Store name and ID based on role
             if (userDetails instanceof AdminModel admin) {
                 session.setAttribute(StringUtils.SESSION_USER_ID, admin.getAdminId());
                 session.setAttribute(StringUtils.SESSION_USER_NAME, admin.getAdminName());
             } else if (userDetails instanceof CustomerModel customer) {
                 session.setAttribute(StringUtils.SESSION_USER_ID, customer.getCustomerId());
                 session.setAttribute(StringUtils.SESSION_USER_NAME, customer.getCustomerName());
             }

            session.setMaxInactiveInterval(30 * 60); // Session timeout 30 minutes

            // Redirect to appropriate dashboard
            redirectToDashboard(response, contextPath, authResult.getUserRole());

        } else {
            // Authentication failed
            LOGGER.warning("Login failed for: " + email);
            request.setAttribute(StringUtils.EMAIL, email); // Keep email in form
            // Determine specific error (could refine LoginService to return codes)
            // For now, use generic message
             request.setAttribute(StringUtils.MESSAGE_ERROR, StringUtils.MESSAGE_ERROR_LOGIN);
            request.getRequestDispatcher(StringUtils.PAGE_URL_LOGIN).forward(request, response);
        }
    }

     private void redirectToDashboard(HttpServletResponse res, String contextPath, String userRole) throws IOException {
         if (StringUtils.ROLE_ADMIN.equals(userRole)) {
             res.sendRedirect(contextPath + StringUtils.SERVLET_URL_ADMIN_DASHBOARD);
         } else if (StringUtils.ROLE_CUSTOMER.equals(userRole)) {
             res.sendRedirect(contextPath + StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD);
         } else {
             // Fallback if role is somehow null/invalid
             LOGGER.warning("Redirecting to login page due to invalid role: " + userRole);
             res.sendRedirect(contextPath + StringUtils.SERVLET_URL_LOGIN);
         }
     }
}