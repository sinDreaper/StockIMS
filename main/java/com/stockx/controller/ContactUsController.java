package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import com.stockx.util.StringUtils;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

@WebServlet(StringUtils.SERVLET_URL_CONTACT_US)
public class ContactUsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
     private static final Logger LOGGER = Logger.getLogger(ContactUsController.class.getName());


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(StringUtils.PAGE_URL_CONTACT_US).forward(request, response);
    }

    /**
     * Handles the contact form submission (Optional).
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO: Implement contact form handling (e.g., send email, save to DB)
    	
        String name = request.getParameter("fullName");
        String email = request.getParameter("emailAddress");
        String subject = request.getParameter("subject");
        String message = request.getParameter("yourMessage");

        LOGGER.info("Contact form submitted by: " + name + " (" + email + ")");
        // Add actual logic here (e.g., email sending)

        request.setAttribute(StringUtils.MESSAGE_SUCCESS, "Your message has been received"); // Placeholder
        request.getRequestDispatcher(StringUtils.PAGE_URL_CONTACT_US).forward(request, response);

    }
}