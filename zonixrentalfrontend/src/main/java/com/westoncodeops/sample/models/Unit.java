package com.westoncodeops.sample.models;

import java.time.LocalDateTime;

/**
 * Client-side domain model representing a Unit entity
 * Maps to backend UnitResponse DTO
 */
public class Unit {
    private Long id;
    private String unitNumber;
    private Double rentAmount;
    private Integer floor;
    private Boolean isOccupied;
    private String tenantName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Unit() {
    }

    public Unit(Long id, String unitNumber, Double rentAmount, Integer floor,
                Boolean isOccupied, String tenantName, LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.id = id;
        this.unitNumber = unitNumber;
        this.rentAmount = rentAmount;
        this.floor = floor;
        this.isOccupied = isOccupied;
        this.tenantName = tenantName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public Double getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(Double rentAmount) {
        this.rentAmount = rentAmount;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        if (isOccupied == null) {
            return "UNKNOWN";
        }
        return isOccupied ? "OCCUPIED" : "AVAILABLE";
    }

    public boolean isAvailable() {
        return Boolean.FALSE.equals(isOccupied);
    }

    public boolean isOccupied() {
        return Boolean.TRUE.equals(isOccupied);
    }

    @Override
    public String toString() {
        return "Unit{" +
                "id=" + id +
                ", unitNumber='" + unitNumber + '\'' +
                ", rentAmount=" + rentAmount +
                ", floor=" + floor +
                ", isOccupied=" + isOccupied +
                ", tenantName='" + tenantName + '\'' +
                '}';
    }
}
