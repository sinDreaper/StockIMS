package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import com.stockx.util.StringUtils;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

@WebServlet(urlPatterns = {"/"}) // ONLY map to root now
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath(); // Get context path

        if (session != null && session.getAttribute(StringUtils.SESSION_USER_EMAIL) != null) {
            // User is logged in, redirect to their dashboard
            String userRole = (String) session.getAttribute(StringUtils.SESSION_USER_ROLE);
             if (StringUtils.ROLE_ADMIN.equals(userRole)) {
                 response.sendRedirect(contextPath + StringUtils.SERVLET_URL_ADMIN_DASHBOARD);
             } else if (StringUtils.ROLE_CUSTOMER.equals(userRole)) {
                 response.sendRedirect(contextPath + StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD);
             } else {
                  // Invalid role in session? Redirect to login (or landing)
                  session.invalidate();
                  response.sendRedirect(contextPath + StringUtils.SERVLET_URL_LANDING); // Redirect to landing
             }
        } else {
            // *** CHANGE HERE: User is not logged in, redirect to landing page servlet ***
            response.sendRedirect(contextPath + StringUtils.SERVLET_URL_LANDING);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}