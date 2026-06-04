package com.westoncodeops.sample.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Ticket {
    private String id;
    private String ticketNumber;
    private String title;
    private String description;
    private String category;
    private String status;
    private String aiPriority;
    private String priority;
    private Long unitId;
    private String unitNumber;
    private String tenantName;
    private String reportedByName;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;

    public Ticket() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getTitle() { return title != null ? title : ticketNumber; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAiPriority() { return aiPriority; }
    public void setAiPriority(String aiPriority) { this.aiPriority = aiPriority; }

    public String getPriority() { return priority != null ? priority : aiPriority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }

    public String getUnitNumber() { return unitNumber; }
    public void setUnitNumber(String unitNumber) { this.unitNumber = unitNumber; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public String getReportedByName() { return reportedByName != null ? reportedByName : tenantName; }
    public void setReportedByName(String reportedByName) { this.reportedByName = reportedByName; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDate getReportedDate() { return reportedAt != null ? reportedAt.toLocalDate() : null; }

    public boolean isResolved() { return "RESOLVED".equalsIgnoreCase(status); }
    public boolean isPending() { return "PENDING".equalsIgnoreCase(status); }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", title='" + getTitle() + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + getPriority() + '\'' +
                ", unitNumber='" + unitNumber + '\'' +
                '}';
    }
}
