package com.westoncodeops.sample.models;

import java.time.LocalDateTime;

/**
 * Client-side domain model representing a Tenant/User entity
 * Maps to backend UserResponse DTO
 */
public class Tenant {
    private String id;
    private String fullname;
    private String email;
    private String phoneNumber;
    private String nationalId;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public Tenant() {
    }

    // Full constructor
    public Tenant(String id, String fullname, String email,
                  String phoneNumber, String nationalId, String role,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.nationalId = nationalId;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        if (fullname == null || fullname.isBlank()) {
            return "";
        }
        String[] parts = fullname.split(" ", 2);
        return parts[0];
    }

    public void setFirstName(String firstName) {
        if (firstName == null) {
            return;
        }
        if (fullname == null || fullname.isBlank()) {
            fullname = firstName;
        } else {
            String[] parts = fullname.split(" ", 2);
            String last = parts.length > 1 ? parts[1] : "";
            fullname = last.isBlank() ? firstName : firstName + " " + last;
        }
    }

    public String getLastName() {
        if (fullname == null || fullname.isBlank()) {
            return "";
        }
        String[] parts = fullname.split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    public void setLastName(String lastName) {
        if (lastName == null) {
            return;
        }
        if (fullname == null || fullname.isBlank()) {
            fullname = lastName;
        } else {
            String first = getFirstName();
            fullname = first + " " + lastName;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getFullName() {
        return fullname == null ? "" : fullname;
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

// Made with Bob
