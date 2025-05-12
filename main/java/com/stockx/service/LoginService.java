package com.stockx.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.stockx.database.DBController;
import com.stockx.model.PasswordEncryptionWithAes;
import com.stockx.model.PasswordModel;
import com.stockx.util.StringUtils;
import com.stockx.util.ValidationUtil;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */


public class LoginService {

    private static final Logger LOGGER = Logger.getLogger(LoginService.class.getName());
    private final DBController dbController;

    public LoginService() {
        this.dbController = new DBController();
    }

    /**
     * Attempts to authenticate a user based on email and password.
     *
     * @param email The user's email.
     * @param plainPassword The user's plain text password.
     * @return A PasswordModel containing user role and ID if successful, null otherwise.
     */
    public PasswordModel authenticateUser(String email, String plainPassword) {
        // Basic validation
        if (ValidationUtil.isNullOrEmpty(email) || !ValidationUtil.isValidEmail(email) ||
            ValidationUtil.isNullOrEmpty(plainPassword)) {
            LOGGER.warning("Login attempt with invalid input format for email: " + email);
            return null; // Invalid input format
        }

        // 1. Retrieve password info from DB based on email
        PasswordModel passwordInfo = dbController.getPasswordInfoByEmail(email);

        if (passwordInfo == null) {
            LOGGER.info("Login attempt failed: Email not found - " + email);
            return null; // Email not found
        }

        // 2. Decrypt the stored password hash
        String storedHash = passwordInfo.getPasswordHash();
        String decryptedPassword = PasswordEncryptionWithAes.decrypt(storedHash, email); // Use email as context

        if (decryptedPassword == null) {
             LOGGER.severe("Password decryption failed for user: " + email + ". Hash might be corrupted or key context mismatch.");
            // Consider this a server error or a specific authentication failure
            return null; // Decryption failed
        }

        // 3. Compare decrypted password with the provided plain password
        if (decryptedPassword.equals(plainPassword)) {
            // Authentication successful
            LOGGER.info("Login successful for user: " + email + " Role: " + passwordInfo.getUserRole());
             // Return the passwordInfo object which contains the role and passwordId
             // We might fetch full user details later using the email and role
            return passwordInfo;
        } else {
            // Password mismatch
            LOGGER.warning("Login attempt failed: Password mismatch for user - " + email);
            return null;
        }
    }

     /**
      * Fetches full user details (Admin or Customer) based on email and role.
      * Should be called *after* successful authentication.
      * @param email User's email.
      * @param role User's role ("Admin" or "Customer").
      * @return AdminModel or CustomerModel object, or null if not found/error.
      */
    public Object getUserDetails(String email, String role) {
        if (ValidationUtil.isNullOrEmpty(email) || ValidationUtil.isNullOrEmpty(role)) {
            return null;
        }
        return dbController.getUserDetailsByEmail(email, role);
    }
}