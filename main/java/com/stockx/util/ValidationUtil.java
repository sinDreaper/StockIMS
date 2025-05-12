package com.stockx.util;

import java.util.regex.Pattern;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

/**
 * Utility class for performing various validation checks on strings.
 */
public class ValidationUtil {

    // Simple email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    // Basic text pattern (allows letters, spaces, hyphens, apostrophes)
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[a-zA-Z\\s'-]+$");

    // Basic number pattern
    private static final Pattern NUMBERS_PATTERN = Pattern.compile("\\d+");

     // Basic phone number pattern (allows digits, spaces, hyphens, parentheses, plus sign)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[\\d\\s\\-\\(\\)\\+]+$");


    /**
     * Validates if the provided text contains only allowed text characters.
     * @param text The text to be validated.
     * @return True if the text is valid, false otherwise.
     */
    public static boolean isValidText(String text) {
        return text != null && TEXT_PATTERN.matcher(text).matches();
    }

    /**
     * Validates if the provided text contains only digits (0-9).
     * @param text The text to be validated.
     * @return True if the text contains only digits, false otherwise.
     */
    public static boolean isNumbersOnly(String text) {
        return text != null && NUMBERS_PATTERN.matcher(text).matches();
    }

     /**
     * Validates if the provided text is a potentially valid phone number.
     * Does not check for specific country formats.
     * @param phone The phone number string to validate.
     * @return True if the format seems plausible, false otherwise.
     */
    public static boolean isValidPhoneNumber(String phone) {
        return phone != null && !phone.trim().isEmpty() && PHONE_PATTERN.matcher(phone).matches();
    }


    /**
     * Validates if the provided text is a valid email address format.
     * @param email The email address to be validated.
     * @return True if the email address has a valid format, false otherwise.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates if the provided password meets basic complexity requirements (e.g., minimum length).
     * Adjust complexity as needed.
     * @param password The password to be validated.
     * @return True if the password meets minimum requirements, false otherwise.
     */
    public static boolean isValidPassword(String password) {
        // Example: At least 6 characters
        return password != null && password.length() >= 6;
    }

    /**
     * Checks if a string is null or empty after trimming whitespace.
     * @param str The string to check.
     * @return True if the string is null or empty, false otherwise.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

     /**
     * Checks if a string represents a valid decimal number.
     * @param str The string to check.
     * @return True if the string is a valid decimal, false otherwise.
     */
    public static boolean isValidDecimal(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

     /**
     * Checks if a string represents a valid integer/long number.
     * @param str The string to check.
     * @return True if the string is a valid integer/long, false otherwise.
     */
    public static boolean isValidLong(String str) {
         if (isNullOrEmpty(str)) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}