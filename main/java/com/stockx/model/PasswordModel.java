package com.stockx.model;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

public class PasswordModel {
    private int passwordId;
    private String email;
    private String passwordHash;
    private String userRole; // "Admin" or "Customer"

    // Constructors
    public PasswordModel() {}

    public PasswordModel(String email, String passwordHash, String userRole) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
    }

    // Getters and Setters
    public int getPasswordId() {
        return passwordId;
    }

    public void setPasswordId(int passwordId) {
        this.passwordId = passwordId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}