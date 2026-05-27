package com.westoncodeops.sample.models;

import java.time.LocalDate;

/**
 * Client-side domain model representing an Extension Request entity
 * Maps to backend ExtensionResponse DTO
 */
public class Extension {
    private Long id;
    private Long tenantId;
    private String tenantName;
    private Long unitId;
    private String unitNumber;
    private LocalDate requestedDate;
    private LocalDate promisedPaymentDate;
    private Double amountDue;
    private String reason;
    private String status;
    private Long approvedById;
    private String approvedByName;
    private LocalDate approvedDate;
    private String rejectionReason;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    // Default constructor
    public Extension() {
    }

    // Full constructor
    public Extension(Long id, Long tenantId, String tenantName, Long unitId, 
                     String unitNumber, LocalDate requestedDate, LocalDate promisedPaymentDate,
                     Double amountDue, String reason, String status, Long approvedById,
                     String approvedByName, LocalDate approvedDate, String rejectionReason,
                     LocalDate createdAt, LocalDate updatedAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.unitId = unitId;
        this.unitNumber = unitNumber;
        this.requestedDate = requestedDate;
        this.promisedPaymentDate = promisedPaymentDate;
        this.amountDue = amountDue;
        this.reason = reason;
        this.status = status;
        this.approvedById = approvedById;
        this.approvedByName = approvedByName;
        this.approvedDate = approvedDate;
        this.rejectionReason = rejectionReason;
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

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public LocalDate getPromisedPaymentDate() {
        return promisedPaymentDate;
    }

    public void setPromisedPaymentDate(LocalDate promisedPaymentDate) {
        this.promisedPaymentDate = promisedPaymentDate;
    }

    public Double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Double amountDue) {
        this.amountDue = amountDue;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(Long approvedById) {
        this.approvedById = approvedById;
    }

    public String getApprovedByName() {
        return approvedByName;
    }

    public void setApprovedByName(String approvedByName) {
        this.approvedByName = approvedByName;
    }

    public LocalDate getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDate approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
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

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(status);
    }

    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Extension{" +
                "id=" + id +
                ", tenantName='" + tenantName + '\'' +
                ", unitNumber='" + unitNumber + '\'' +
                ", promisedPaymentDate=" + promisedPaymentDate +
                ", amountDue=" + amountDue +
                ", status='" + status + '\'' +
                '}';
    }
}

// Made with Bob
