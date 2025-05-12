package com.stockx.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.stockx.util.StringUtils;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

@WebServlet(StringUtils.SERVLET_URL_ABOUT_US)
public class AboutUsController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(StringUtils.PAGE_URL_ABOUT_US).forward(request, response);
    }

    // No POST expected for About Us
    // protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //     doGet(request, response);
    // }
}