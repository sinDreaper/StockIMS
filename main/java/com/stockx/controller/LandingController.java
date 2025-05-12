package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// No need for HttpSession import here if we rely on the filter
// import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import com.stockx.util.StringUtils;
/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

/**
 * Servlet responsible for displaying the public landing page.
 * It forwards requests to the landing.jsp file.
 * Assumes the AuthenticationFilter handles redirecting logged-in users
 * away from this page.
 */
@WebServlet(StringUtils.SERVLET_URL_LANDING) // Mapped to "/landing" via StringUtils
public class LandingController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests by forwarding to the landing page JSP.
     *
     * @param request  The HttpServletRequest object.
     * @param response The HttpServletResponse object.
     * @throws ServletException If a servlet-specific error occurs.
     * @throws IOException      If an I/O error occurs during forwarding.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Simple forward to the JSP page defined in StringUtils
        // The AuthenticationFilter should prevent logged-in users from reaching here directly
        // if configured to do so.
        request.getRequestDispatcher(StringUtils.PAGE_URL_LANDING).forward(request, response);
    }

    /**
     * Handles POST requests, typically just redirects to doGet as landing pages
     * usually don't process POST data directly unless there's a specific form (like newsletter signup).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // For a simple landing page, just delegate to doGet
        doGet(request, response);
    }
}