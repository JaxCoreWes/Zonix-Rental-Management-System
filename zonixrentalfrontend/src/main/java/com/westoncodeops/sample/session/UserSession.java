package com.westoncodeops.sample.session;

/**
 * Global session manager for maintaining authenticated user state across the application.
 * Implements Singleton pattern to ensure single instance throughout application lifecycle.
 * 
 * SECURITY NOTE: In production, consider encrypting sensitive data and implementing
 * session timeout mechanisms.
 */
public class UserSession {
    private static UserSession instance;
    
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role; // LANDLORD, CARETAKER, TENANT, ADMIN
    private String authToken;
    private boolean isAuthenticated;
    
    /**
     * Private constructor to prevent direct instantiation
     */
    private UserSession() {
        this.isAuthenticated = false;
    }
    
    /**
     * Get the singleton instance of UserSession
     * @return The single UserSession instance
     */
    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize user session after successful authentication
     * @param userId User's unique identifier
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @param phoneNumber User's phone number
     * @param role User's role (LANDLORD, CARETAKER, TENANT, ADMIN)
     * @param authToken JWT or session token for API authentication
     */
    public void login(String userId, String firstName, String lastName, String email, 
                     String phoneNumber, String role, String authToken) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.authToken = authToken;
        this.isAuthenticated = true;
    }
    
    /**
     * Clear all session data and mark user as unauthenticated
     */
    public void logout() {
        this.userId = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.phoneNumber = null;
        this.role = null;
        this.authToken = null;
        this.isAuthenticated = false;
    }
    
    /**
     * Check if user is currently authenticated
     * @return true if user is logged in, false otherwise
     */
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
    
    /**
     * Get the full name of the logged-in user
     * @return Full name (firstName + lastName)
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return "Guest";
    }
    
    /**
     * Check if the current user has LANDLORD role
     * @return true if user is a landlord
     */
    public boolean isLandlord() {
        return "LANDLORD".equalsIgnoreCase(role);
    }
    
    /**
     * Check if the current user has CARETAKER role
     * @return true if user is a caretaker
     */
    public boolean isCaretaker() {
        return "CARETAKER".equalsIgnoreCase(role);
    }
    
    /**
     * Check if the current user has TENANT role
     * @return true if user is a tenant
     */
    public boolean isTenant() {
        return "TENANT".equalsIgnoreCase(role);
    }
    
    /**
     * Check if the current user has ADMIN role
     * @return true if user is an admin
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
    
    /**
     * Check if user has permission to access payment extensions module
     * Only LANDLORD can approve/reject extensions
     * @return true if user can access extensions
     */
    public boolean canAccessExtensions() {
        return isLandlord() || isAdmin();
    }
    
    /**
     * Check if user has permission to approve payment extensions
     * Only LANDLORD has approval authority
     * @return true if user can approve extensions
     */
    public boolean canApproveExtensions() {
        return isLandlord();
    }
    
    /**
     * Check if user has permission to manage tenants (CRUD operations)
     * LANDLORD, CARETAKER and ADMIN can manage tenants
     * @return true if user can manage tenants
     */
    public boolean canManageTenants() {
        return isLandlord() || isCaretaker() || isAdmin();
    }
    
    /**
     * Check if user has permission to manage units
     * LANDLORD, CARETAKER and ADMIN can manage units
     * @return true if user can manage units
     */
    public boolean canManageUnits() {
        return isLandlord() || isCaretaker() || isAdmin();
    }
    
    /**
     * Check if user has permission to manage maintenance tickets
     * LANDLORD, CARETAKER and ADMIN can manage tickets
     * @return true if user can manage tickets
     */
    public boolean canManageTickets() {
        return isLandlord() || isCaretaker() || isAdmin();
    }
    
    /**
     * Check if user has permission to view payments
     * LANDLORD and ADMIN can view payments
     * @return true if user can view payments
     */
    public boolean canViewPayments() {
        return isLandlord() || isAdmin();
    }
    
    // Getters
    public String getUserId() {
        return userId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getRole() {
        return role;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    @Override
    public String toString() {
        return "UserSession{" +
                "userId=" + userId +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", isAuthenticated=" + isAuthenticated +
                '}';
    }
}

// Made with Bob