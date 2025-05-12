package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

import com.stockx.database.DBController; // Use DBController to fetch details
import com.stockx.util.StringUtils;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

@WebServlet(StringUtils.SERVLET_URL_USER_PROFILE)
public class UserProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UserProfileController.class.getName());
    private DBController dbController;

     @Override
    public void init() throws ServletException {
        super.init();
        this.dbController = new DBController();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(StringUtils.SESSION_USER_EMAIL) == null) {
            // Should be caught by filter, but double-check
             response.sendRedirect(request.getContextPath() + StringUtils.SERVLET_URL_LOGIN);
            return;
        }

        String userEmail = (String) session.getAttribute(StringUtils.SESSION_USER_EMAIL);
        String userRole = (String) session.getAttribute(StringUtils.SESSION_USER_ROLE);

        // Fetch user details based on role and email
        Object userDetails = dbController.getUserDetailsByEmail(userEmail, userRole);

        if (userDetails != null) {
            request.setAttribute(StringUtils.REQ_ATTRIBUTE_USER, userDetails);
        } else {
             LOGGER.warning("Could not retrieve profile details for user: " + userEmail);
            request.setAttribute(StringUtils.MESSAGE_ERROR, "Failed to load profile information.");
        }

        request.getRequestDispatcher(StringUtils.PAGE_URL_USER_PROFILE).forward(request, response);
    }

    /**
     * Handles profile update form submission (Optional - not implemented in detail)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Implement profile update logic
        // 1. Get parameters (name, contact, dob etc.)
        // 2. Validate input
        // 3. Get user ID and role from session
        // 4. Create corresponding update query in DBController (e.g., updateCustomerDetails, updateAdminDetails)
        // 5. Call DBController method
        // 6. Set success/error message
        // 7. Update session name if changed: session.setAttribute(StringUtils.SESSION_USER_NAME, newName);
        // 8. Redirect back to profile page (use GET to prevent re-submission)

        HttpSession session = request.getSession(false);
         if (session != null) {
            session.setAttribute(StringUtils.MESSAGE_SUCCESS, "Profile update submitted (functionality pending)."); // Placeholder message
         }
         response.sendRedirect(request.getContextPath() + StringUtils.SERVLET_URL_USER_PROFILE); // Redirect after POST
    }
}