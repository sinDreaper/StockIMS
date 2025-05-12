package com.stockx.model;

/**
 * @author Prashant Rijal
 * LMU ID: 23048683
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockModel {
    private int stockId;
    private String symbol;
    private String companyName;
    private String instrumentId;
    private Long totalShares;
    private LocalDate listingDate;
    private BigDecimal openingPrice;
    private BigDecimal closingPrice;
    private String category;
    private String exchange;
    private String status; // "Active", "Inactive"
    private LocalDateTime dateAdded;
    private LocalDateTime lastUpdated;
    

    // Constructors
    public StockModel() {}

    // Getters and Setters
    public int getStockId() { return stockId; }
    public void setStockId(int stockId) { this.stockId = stockId; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getInstrumentId() { return instrumentId; }
    public void setInstrumentId(String instrumentId) { this.instrumentId = instrumentId; }

    public Long getTotalShares() { return totalShares; }
    public void setTotalShares(Long totalShares) { this.totalShares = totalShares; }

    public LocalDate getListingDate() { return listingDate; }
    public void setListingDate(LocalDate listingDate) { this.listingDate = listingDate; }

    public BigDecimal getOpeningPrice() { return openingPrice; }
    public void setOpeningPrice(BigDecimal openingPrice) { this.openingPrice = openingPrice; }
    // Helper for setting from String safely
     public void setOpeningPriceFromString(String priceStr) {
        try {
            this.openingPrice = (priceStr == null || priceStr.trim().isEmpty()) ? null : new BigDecimal(priceStr);
        } catch (NumberFormatException e) {
            this.openingPrice = null; // Or handle error appropriately
        }
    }


    public BigDecimal getClosingPrice() { return closingPrice; }
    public void setClosingPrice(BigDecimal closingPrice) { this.closingPrice = closingPrice; }
     // Helper for setting from String safely
    public void setClosingPriceFromString(String priceStr) {
        try {
            this.closingPrice = (priceStr == null || priceStr.trim().isEmpty()) ? null : new BigDecimal(priceStr);
        } catch (NumberFormatException e) {
            this.closingPrice = null; // Or handle error appropriately
        }
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    // Helper method to get a display-friendly price (e.g., closing price if available)
    public BigDecimal getCurrentPrice() {
        return (closingPrice != null) ? closingPrice : openingPrice;
    }
    
    public String getDateAddedFormatted() {
        if (this.dateAdded == null) {
            return "N/A"; // Or return "" or handle as needed
        }
        // Define the desired format (Import java.time.format.DateTimeFormatter)
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.dateAdded.format(formatter);
    }
    
        
}