package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

import com.stockx.util.StringUtils;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

@WebServlet(StringUtils.SERVLET_URL_LOGOUT)
public class LogoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LogoutController.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirect POST requests to doGet for simplicity, or handle directly if needed
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Get existing session, don't create new

        if (session != null) {
             String email = (String) session.getAttribute(StringUtils.SESSION_USER_EMAIL);
             LOGGER.info("Logging out user: " + (email != null ? email : "Unknown"));
            session.invalidate(); // Invalidate the session
        } else {
            LOGGER.info("Logout attempt without an active session.");
        }

        // Redirect to the login page
        response.sendRedirect(request.getContextPath() + StringUtils.SERVLET_URL_LOGIN);
    }
}