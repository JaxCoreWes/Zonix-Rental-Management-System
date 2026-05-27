package com.westoncodeops.sample.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Client-side domain model representing a Payment entity
 * Maps to backend PaymentResponse DTO
 */
public class Payment {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private Long unitId;
    private String unitNumber;
    private Double amount;
    private String paymentMethod;
    private String transactionReference;
    private String mpesaReceiptNumber;
    private String phoneNumber;
    private String status;
    private LocalDateTime paymentDate;
    private String paymentFor;
    private String notes;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // Default constructor
    public Payment() {
    }

    // Full constructor
    public Payment(Long id, Long tenantId, String tenantName, Long unitId, 
                   String unitNumber, Double amount, String paymentMethod,
                   String transactionReference, String mpesaReceiptNumber, 
                   String phoneNumber, String status, LocalDateTime paymentDate,
                   String paymentFor, String notes, LocalDate createdAt, LocalDate updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.unitId = unitId;
        this.unitNumber = unitNumber;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
        this.mpesaReceiptNumber = mpesaReceiptNumber;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.paymentDate = paymentDate;
        this.paymentFor = paymentFor;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getMpesaReceiptNumber() {
        return mpesaReceiptNumber;
    }

    public void setMpesaReceiptNumber(String mpesaReceiptNumber) {
        this.mpesaReceiptNumber = mpesaReceiptNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentFor() {
        return paymentFor;
    }

    public void setPaymentFor(String paymentFor) {
        this.paymentFor = paymentFor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", tenantName='" + tenantName + '\'' +
                ", unitNumber='" + unitNumber + '\'' +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                ", mpesaReceiptNumber='" + mpesaReceiptNumber + '\'' +
                '}';
    }
}

// Made with Bob
