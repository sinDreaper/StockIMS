package com.stockx.model;

import java.time.LocalDate;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

public class CustomerModel {
    private int customerId;
    private String customerName;
    private String customerEmail;
    private LocalDate customerDOB;
    private String customerContact;
    private int passwordId; // FK

    // Constructors
    public CustomerModel() {}

    public CustomerModel(String customerName, String customerEmail, LocalDate customerDOB, String customerContact, int passwordId) {
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerDOB = customerDOB;
        this.customerContact = customerContact;
        this.passwordId = passwordId;
    }

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public LocalDate getCustomerDOB() { return customerDOB; }
    public void setCustomerDOB(LocalDate customerDOB) { this.customerDOB = customerDOB; }

    public String getCustomerContact() { return customerContact; }
    public void setCustomerContact(String customerContact) { this.customerContact = customerContact; }

    public int getPasswordId() { return passwordId; }
    public void setPasswordId(int passwordId) { this.passwordId = passwordId; }
}