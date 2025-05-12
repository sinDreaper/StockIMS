package com.stockx.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;

import com.stockx.database.DBController;
import com.stockx.model.CustomerModel;
import com.stockx.util.StringUtils;
import com.stockx.util.ValidationUtil;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

public class RegisterService {

    private static final Logger LOGGER = Logger.getLogger(RegisterService.class.getName());
    private final DBController dbController;

    public RegisterService() {
        this.dbController = new DBController();
    }

    /**
     * Registers a new customer after performing validation checks.
     *
     * @param name Customer's full name.
     * @param email Customer's email.
     * @param password Plain text password.
     * @param retypePassword Retyped password for confirmation.
     * @param dobString Date of birth as String (YYYY-MM-DD). Optional.
     * @param contactNumber Customer's contact number. Optional.
     * @return A StringUtils constant indicating success or specific error type.
     */
    public String registerCustomer(String name, String email, String password, String retypePassword, String dobString, String contactNumber) {

        // 1. Input Validation
        if (ValidationUtil.isNullOrEmpty(name) || !ValidationUtil.isValidText(name)) {
             return StringUtils.MESSAGE_ERROR_INVALID_DATA + " (Name)";
        }
        if (ValidationUtil.isNullOrEmpty(email) || !ValidationUtil.isValidEmail(email)) {
             return StringUtils.MESSAGE_ERROR_INVALID_DATA + " (Email Format)";
        }
        if (ValidationUtil.isNullOrEmpty(password) || !ValidationUtil.isValidPassword(password)) {
            // Assuming isValidPassword checks minimum length etc.
             return StringUtils.MESSAGE_ERROR_INVALID_DATA + " (Password Format/Length)";
        }
        if (!password.equals(retypePassword)) {
            return StringUtils.MESSAGE_ERROR_PASSWORD_UNMATCHED;
        }
        // Optional fields validation
        LocalDate dob = null;
        if (!ValidationUtil.isNullOrEmpty(dobString)) {
            try {
                dob = LocalDate.parse(dobString); // Expects YYYY-MM-DD
            } catch (DateTimeParseException e) {
                return StringUtils.MESSAGE_ERROR_INVALID_DATA + " (Invalid Date Format)";
            }
        }
         if (!ValidationUtil.isNullOrEmpty(contactNumber) && !ValidationUtil.isValidPhoneNumber(contactNumber)) {
             return StringUtils.MESSAGE_ERROR_INVALID_DATA + " (Invalid Contact Number Format)";
         }

        // 2. Check if email already exists (handled by DBController now, but can double-check here)
        // if (dbController.checkEmailExists(email)) {
        //     return StringUtils.MESSAGE_ERROR_EMAIL_EXISTS;
        // }

        // 3. Create Customer Model
        CustomerModel customer = new CustomerModel();
        customer.setCustomerName(name.trim());
        customer.setCustomerEmail(email.trim().toLowerCase()); // Store email consistently
        customer.setCustomerDOB(dob);
        customer.setCustomerContact(contactNumber != null ? contactNumber.trim() : null);
        // Password ID will be set during DB insertion

        // 4. Call DB Controller to register
        int result = dbController.registerCustomer(customer, password);

        // 5. Interpret result
        switch (result) {
            case 1:
                return StringUtils.MESSAGE_SUCCESS_REGISTER;
            case 0:
                // This now specifically means email exists based on DBController logic
                return StringUtils.MESSAGE_ERROR_EMAIL_EXISTS;
            case -1:
            default:
                // Server/DB error
                return StringUtils.MESSAGE_ERROR_SERVER;
        }
    }
}