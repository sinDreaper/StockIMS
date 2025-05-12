package com.stockx.model;

import java.time.LocalDate;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

public class AdminModel {
    private int adminId;
    private String adminName;
    private String adminEmail;
    private LocalDate adminDOB;
    private String adminContact;
    private int passwordId; // FK

    // Constructors
    public AdminModel() {}

     public AdminModel(String adminName, String adminEmail, LocalDate adminDOB, String adminContact, int passwordId) {
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.adminDOB = adminDOB;
        this.adminContact = adminContact;
        this.passwordId = passwordId;
    }

    // Getters and Setters
    public int getAdminId() { return adminId; }
    public void setAdminId(int adminId) { this.adminId = adminId; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public LocalDate getAdminDOB() { return adminDOB; }
    public void setAdminDOB(LocalDate adminDOB) { this.adminDOB = adminDOB; }

    public String getAdminContact() { return adminContact; }
    public void setAdminContact(String adminContact) { this.adminContact = adminContact; }

    public int getPasswordId() { return passwordId; }
    public void setPasswordId(int passwordId) { this.passwordId = passwordId; }
}