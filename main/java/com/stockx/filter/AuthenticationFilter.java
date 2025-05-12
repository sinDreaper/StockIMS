package com.stockx.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.stockx.util.StringUtils; // Make sure this import is correct

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

/**
 * Authentication and Authorization Filter for the StockX application.
 * - Ensures users are logged in to access protected resources.
 * - Enforces role-based access (Admin/Customer).
 * - Allows public access to specific pages (Landing, Login, Register, Static Resources).
 */
@WebFilter("/*") // Apply filter to all incoming requests
public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());

    // Define sets of URL patterns for different access levels
    // Using constants from StringUtils for maintainability

    // URLs accessible WITHOUT login
    private static final Set<String> PUBLIC_URLS = new HashSet<>(Arrays.asList(
            StringUtils.SERVLET_URL_LOGIN,       // /login (Servlet)
            StringUtils.PAGE_URL_LOGIN,          // /WEB-INF/pages/login.jsp (Direct access blocked by WEB-INF anyway)
            StringUtils.SERVLET_URL_REGISTER,    // /register (Servlet)
            StringUtils.PAGE_URL_REGISTER,       // /WEB-INF/pages/register.jsp
            StringUtils.SERVLET_URL_LANDING,     // /landing (Servlet for public landing page)
            StringUtils.PAGE_URL_LANDING,        // /WEB-INF/pages/landing.jsp
            StringUtils.SERVLET_URL_ABOUT_US,    // /aboutus (Assuming public)
            StringUtils.PAGE_URL_ABOUT_US,       // /WEB-INF/pages/about_us.jsp
            StringUtils.SERVLET_URL_CONTACT_US,  // /contactus (Assuming public)
            StringUtils.PAGE_URL_CONTACT_US      // /WEB-INF/pages/contact_us.jsp
    ));

     // URLs accessible ONLY by users with the 'Admin' role
     private static final Set<String> ADMIN_URLS = new HashSet<>(Arrays.asList(
            StringUtils.SERVLET_URL_ADMIN_DASHBOARD, // /admin_dashboard (Servlet)
            StringUtils.PAGE_URL_ADMIN_DASHBOARD,    // /WEB-INF/pages/admin_dashboard.jsp
            StringUtils.SERVLET_URL_STOCK_ACTION     // /admin/stock (Servlet for CRUD)
     ));

     // URLs accessible ONLY by users with the 'Customer' role (Admin also allowed implicitly later)
      private static final Set<String> CUSTOMER_URLS = new HashSet<>(Arrays.asList(
             StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD, // /customer_dashboard (Servlet)
             StringUtils.PAGE_URL_CUSTOMER_DASHBOARD     // /WEB-INF/pages/customer_dashboard.jsp
             // Add other customer-specific URLs like portfolio view if needed
      ));

     // URLs accessible by ANY logged-in user (Admin or Customer)
      private static final Set<String> LOGGED_IN_URLS = new HashSet<>(Arrays.asList(
             StringUtils.SERVLET_URL_LOGOUT,        // /logout (Servlet)
             StringUtils.SERVLET_URL_USER_PROFILE,  // /user_profile (Servlet)
             StringUtils.PAGE_URL_USER_PROFILE      // /WEB-INF/pages/user_profile.jsp
             // Add other common pages like settings if applicable
      ));

    // Prefixes for static resources that should always be allowed
    private static final String[] STATIC_RESOURCE_PREFIXES = {
            "/css/", "/js/", "/images/", "/resources/" // Adjust if you use different folders
    };


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("AuthenticationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false); // Get session IF it exists, don't create one

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        // Path relative to the context root (e.g., "/login", "/admin_dashboard", "/css/style.css")
        String path = requestURI.substring(contextPath.length());

        // --- Step 1: Allow Static Resources ---
        if (isStaticResource(path)) {
            chain.doFilter(request, response); // Allow access and stop filter processing
            return;
        }

        // --- Step 2: Check Login Status ---
        boolean isLoggedIn = (session != null && session.getAttribute(StringUtils.SESSION_USER_EMAIL) != null);
        String userRole = isLoggedIn ? (String) session.getAttribute(StringUtils.SESSION_USER_ROLE) : null;

        // --- Step 3: Handle Public URLs ---
        // Also includes the root path "/" handled by HomeController which redirects appropriately
        if (isPublicResource(path) || "/".equals(path)) {
             // If user is ALREADY logged in and tries to access public pages like login, register, landing
             // redirect them to their dashboard to avoid confusion.
             if (isLoggedIn && (
                 path.equals(StringUtils.SERVLET_URL_LOGIN) ||
                 path.equals(StringUtils.SERVLET_URL_REGISTER) ||
                 path.equals(StringUtils.SERVLET_URL_LANDING)
                 // No need to check PAGE_URL constants as they are in WEB-INF
                 ))
             {
                 LOGGER.fine("Logged in user (" + userRole + ") accessing public auth page (" + path + "). Redirecting to dashboard.");
                 redirectToDashboard(res, contextPath, userRole);
                 return;
             }
             // Otherwise, allow access to the public resource
             chain.doFilter(request, response);
             return;
        }

        // --- Step 4: Handle Protected URLs - User MUST be logged in ---
        if (!isLoggedIn) {
            LOGGER.fine("User not logged in, attempting to access protected resource: " + path + ". Redirecting.");
            // Redirect to the public landing page. Add a message or redirect param if needed.
            res.sendRedirect(contextPath + StringUtils.SERVLET_URL_LANDING + "?message=login_required");
            // Alternatively, redirect directly to login:
            // res.sendRedirect(contextPath + StringUtils.SERVLET_URL_LOGIN + "?redirect=" + path);
            return;
        }

        // --- Step 5: Handle Protected URLs - Role-Based Authorization ---
        // User is definitely logged in here. Now check roles.

        // Check Admin-Only URLs
        if (isAdminResource(path)) {
            if (StringUtils.ROLE_ADMIN.equals(userRole)) {
                // User is Admin and accessing Admin resource - Allow
                chain.doFilter(request, response);
            } else {
                // User is logged in but NOT Admin, trying to access Admin resource - Deny
                LOGGER.warning("Non-Admin user (" + session.getAttribute(StringUtils.SESSION_USER_EMAIL) + ") " +
                               "attempting to access Admin resource: " + path);
                // Redirect to their own dashboard or show a forbidden error
                // Option A: Redirect to their dashboard
                 req.setAttribute(StringUtils.MESSAGE_ERROR, StringUtils.MESSAGE_ERROR_NOT_AUTHORIZED);
                 redirectToDashboard(res, contextPath, userRole);
                 // Option B: Send HTTP Forbidden status
                 // res.sendError(HttpServletResponse.SC_FORBIDDEN, StringUtils.MESSAGE_ERROR_NOT_AUTHORIZED);
            }
            return; // Stop filter processing
        }

        // Check Customer URLs (Allow Admins too, as Admins can typically do everything)
        if (isCustomerResource(path)) {
             if (StringUtils.ROLE_CUSTOMER.equals(userRole) || StringUtils.ROLE_ADMIN.equals(userRole)) {
                 // User is Customer OR Admin accessing Customer resource - Allow
                 chain.doFilter(request, response);
             } else {
                 // Should not happen with valid roles, but handle defensively
                 LOGGER.severe("User with unexpected role ("+ userRole +") accessing customer resource: " + path);
                 res.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
             }
             return; // Stop filter processing
        }

        // Check URLs accessible by ANY logged-in user
        if(isLoggedInResource(path)){
             chain.doFilter(request, response); // Allow access
             return; // Stop filter processing
        }

        // --- Step 6: Handle Unmatched URLs for Logged-in Users ---
        // If the URL didn't match public, admin, customer, or common logged-in patterns
        // It might be a 404 or a configuration error.
        LOGGER.log(Level.WARNING, "Access attempt by logged-in user (" + userRole + ") to potentially " +
                                   "undefined or restricted resource: " + path);

        // Option: Allow Admins potentially broader access, restrict others
        // if (StringUtils.ROLE_ADMIN.equals(userRole)) {
        //     chain.doFilter(request, response); // Let Admin proceed, might result in 404 if resource truly doesn't exist
        // } else {
        //     res.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested resource was not found.");
        // }

        // Simpler Option: Treat all unmatched as Not Found for logged-in users too
        res.sendError(HttpServletResponse.SC_NOT_FOUND, "The requested resource ("+ path +") was not found.");

    } // end doFilter


    /**
     * Checks if the requested path starts with any of the defined static resource prefixes.
     * @param path The path relative to the context root.
     * @return True if it's considered a static resource, false otherwise.
     */
    private boolean isStaticResource(String path) {
        if (path == null) return false;
        for (String prefix : STATIC_RESOURCE_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the path corresponds to a defined public URL.
     * @param path The path relative to the context root.
     * @return True if the path is public, false otherwise.
     */
    private boolean isPublicResource(String path) {
        // Check exact matches for servlets/JSPs (JSP paths are mainly for completeness, WEB-INF blocks direct access)
        return PUBLIC_URLS.contains(path);
        // Note: Root path "/" is handled separately in doFilter as it involves HomeController logic
    }

    /**
     * Checks if the path corresponds to an Admin-only URL or prefix.
     * @param path The path relative to the context root.
     * @return True if the path requires Admin role, false otherwise.
     */
    private boolean isAdminResource(String path) {
        if (ADMIN_URLS.contains(path)) return true;
         // Check for prefixes if you organize admin actions under a common path like "/admin/"
         if (path.startsWith("/admin/")) return true; // Example: Covers /admin/stock
        return false;
    }

    /**
     * Checks if the path corresponds to a Customer URL.
     * @param path The path relative to the context root.
     * @return True if the path is specific to Customers, false otherwise.
     */
     private boolean isCustomerResource(String path) {
         if (CUSTOMER_URLS.contains(path)) return true;
          // Check for prefixes if needed, e.g., path.startsWith("/customer/")
         return false;
    }

    /**
     * Checks if the path corresponds to a URL accessible by any logged-in user.
     * @param path The path relative to the context root.
     * @return True if accessible by any logged-in user, false otherwise.
     */
     private boolean isLoggedInResource(String path) {
         return LOGGED_IN_URLS.contains(path);
     }

    /**
     * Redirects the user to the appropriate dashboard based on their role.
     * @param res The HttpServletResponse object.
     * @param contextPath The application's context path.
     * @param userRole The role of the user ("Admin" or "Customer").
     * @throws IOException If a redirection error occurs.
     */
     private void redirectToDashboard(HttpServletResponse res, String contextPath, String userRole) throws IOException {
         String targetUrl;
         if (StringUtils.ROLE_ADMIN.equals(userRole)) {
             targetUrl = contextPath + StringUtils.SERVLET_URL_ADMIN_DASHBOARD;
         } else if (StringUtils.ROLE_CUSTOMER.equals(userRole)) {
             targetUrl = contextPath + StringUtils.SERVLET_URL_CUSTOMER_DASHBOARD;
         } else {
             // Fallback if role is somehow null/invalid when supposedly logged in
             LOGGER.warning("redirectToDashboard called with invalid role: " + userRole + ". Redirecting to landing.");
             targetUrl = contextPath + StringUtils.SERVLET_URL_LANDING; // Redirect to public landing
         }
         res.sendRedirect(targetUrl);
     }


    @Override
    public void destroy() {
        LOGGER.info("AuthenticationFilter destroyed.");
    }
}