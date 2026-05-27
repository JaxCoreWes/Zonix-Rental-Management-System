# ZonixRental JavaFX - Spring Boot API Integration Guide

## 📋 Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Authentication & Session Management](#authentication--session-management)
4. [REST Client Implementation](#rest-client-implementation)
5. [API Endpoint Mapping](#api-endpoint-mapping)
6. [Role-Based Access Control](#role-based-access-control)
7. [Error Handling](#error-handling)
8. [Scaling Guidelines](#scaling-guidelines)
9. [Testing](#testing)
10. [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

This document provides a comprehensive technical guide for the JavaFX frontend integration with the Spring Boot backend REST API. The system implements strict role-based access control (RBAC) for two primary desktop user types: **LANDLORD** and **CARETAKER**.

### Key Features
- ✅ Native Java HttpClient for REST communication
- ✅ Automatic authentication token injection
- ✅ Role-based UI component rendering
- ✅ Asynchronous API calls with JavaFX Platform threading
- ✅ Comprehensive error handling and user feedback
- ✅ JSON serialization/deserialization with Gson

---

## 🏗️ Architecture

### Component Hierarchy

```
┌─────────────────────────────────────────────────────────┐
│                    JavaFX Frontend                       │
├─────────────────────────────────────────────────────────┤
│  Controllers (UI Logic)                                  │
│  ├── MainController (Navigation & RBAC)                 │
│  ├── DashboardController (Role-based metrics)           │
│  ├── TenantController (CARETAKER only)                  │
│  ├── UnitController (CARETAKER only)                    │
│  ├── TicketController (CARETAKER only)                  │
│  ├── ExtensionController (LANDLORD only)                │
│  └── PaymentController (LANDLORD only)                  │
├─────────────────────────────────────────────────────────┤
│  Session Management                                      │
│  └── UserSession (Singleton - Auth & Role state)        │
├─────────────────────────────────────────────────────────┤
│  Network Layer                                           │
│  └── RestClient (HTTP communication)                    │
├─────────────────────────────────────────────────────────┤
│  Models (DTOs)                                           │
│  ├── Tenant, Unit, Ticket, Extension, Payment           │
└─────────────────────────────────────────────────────────┘
                          ↕ HTTP/JSON
┌─────────────────────────────────────────────────────────┐
│              Spring Boot Backend (Port 8080)             │
│  REST API Endpoints: /api/v1/*                          │
└─────────────────────────────────────────────────────────┘
```

---

## 🔐 Authentication & Session Management

### UserSession Singleton

The `UserSession` class maintains the authenticated user's state throughout the application lifecycle.

**Location:** `com.westoncodeops.sample.session.UserSession`

#### Key Methods

```java
// Initialize session after successful login
UserSession.getInstance().login(
    userId,
    firstName,
    lastName,
    email,
    phoneNumber,
    role,        // "LANDLORD" or "CARETAKER"
    authToken    // JWT or session token
);

// Check authentication status
boolean isLoggedIn = UserSession.getInstance().isAuthenticated();

// Get current user role
String role = UserSession.getInstance().getRole();

// Check permissions
boolean canApprove = UserSession.getInstance().canApproveExtensions();

// Clear session on logout
UserSession.getInstance().logout();
```

#### Role Permission Matrix

| Permission Method | LANDLORD | CARETAKER | Description |
|------------------|----------|-----------|-------------|
| `canAccessExtensions()` | ✅ | ❌ | View payment extensions |
| `canApproveExtensions()` | ✅ | ❌ | Approve/reject extensions |
| `canViewPayments()` | ✅ | ❌ | View M-Pesa transactions |
| `canManageTenants()` | ❌ | ✅ | CRUD operations on tenants |
| `canManageUnits()` | ❌ | ✅ | Update unit status |
| `canManageTickets()` | ❌ | ✅ | Resolve maintenance tickets |

---

## 🌐 REST Client Implementation

### RestClient Utility

**Location:** `com.westoncodeops.sample.network.RestClient`

The `RestClient` class provides a unified interface for all HTTP operations with automatic authentication header injection.

#### Configuration

```java
private static final String BASE_URL = "http://localhost:8080/api/v1";
private static final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
```

#### HTTP Methods

##### GET Request
```java
// Fetch list of tenants
String response = RestClient.get("/users", String.class);
List<Tenant> tenants = RestClient.getGson().fromJson(
    response, 
    new TypeToken<List<Tenant>>(){}.getType()
);
```

##### POST Request
```java
// Register new tenant
Map<String, Object> requestData = new HashMap<>();
requestData.put("firstName", "John");
requestData.put("lastName", "Doe");
requestData.put("email", "john@example.com");
requestData.put("role", "TENANT");

RestClient.post("/users/register", requestData, String.class);
```

##### PUT Request
```java
// Update unit status
Map<String, String> requestBody = new HashMap<>();
requestBody.put("status", "OCCUPIED");

RestClient.put("/units/5/status", requestBody, String.class);
```

##### DELETE Request
```java
// Delete resource
RestClient.delete("/users/10");
```

#### Authentication Header Injection

The `RestClient` automatically adds authentication headers to all requests:

```java
private static HttpRequest.Builder addAuthHeaders(HttpRequest.Builder builder) {
    builder.header("Content-Type", "application/json");
    
    UserSession session = UserSession.getInstance();
    if (session.isAuthenticated() && session.getAuthToken() != null) {
        builder.header("Authorization", "Bearer " + session.getAuthToken());
    }
    
    return builder;
}
```

---

## 🔗 API Endpoint Mapping

### Complete Endpoint Reference

#### 1. Users/Tenants Module

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/users` | Fetch all active tenants | CARETAKER |
| POST | `/api/v1/users/register` | Register new tenant | CARETAKER |

**Example Implementation:**
```java
// In TenantController.java
public void loadTenants() {
    new Thread(() -> {
        try {
            String response = RestClient.get("/users", String.class);
            List<Tenant> tenants = RestClient.getGson().fromJson(
                response, 
                new TypeToken<List<Tenant>>(){}.getType()
            );
            
            Platform.runLater(() -> {
                tenantsList.clear();
                tenantsList.addAll(tenants);
                updateUI();
            });
        } catch (Exception e) {
            Platform.runLater(() -> showError(e.getMessage()));
        }
    }).start();
}
```

#### 2. Units Module

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/units` | Fetch all units | CARETAKER |
| PUT | `/api/v1/units/{id}/status` | Toggle unit status (AVAILABLE ↔ OCCUPIED) | CARETAKER |

**Example Implementation:**
```java
// In UnitController.java
private void handleToggleUnitStatus(Unit unit) {
    new Thread(() -> {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("status", unit.isAvailable() ? "OCCUPIED" : "AVAILABLE");
            
            RestClient.put("/units/" + unit.getId() + "/status", requestBody, String.class);
            
            Platform.runLater(() -> {
                showInfo("Unit status updated successfully");
                loadUnits();
            });
        } catch (Exception e) {
            Platform.runLater(() -> showError(e.getMessage()));
        }
    }).start();
}
```

#### 3. Maintenance Tickets Module

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/maintenance` | Fetch all maintenance tickets | CARETAKER |
| PUT | `/api/v1/maintenance/{id}/resolve` | Mark ticket as resolved | CARETAKER |

**Example Implementation:**
```java
// In TicketController.java
private void handleResolveTicket(Ticket ticket) {
    new Thread(() -> {
        try {
            RestClient.put("/maintenance/" + ticket.getId() + "/resolve", null, String.class);
            
            Platform.runLater(() -> {
                showInfo("Ticket resolved successfully");
                loadTickets();
            });
        } catch (Exception e) {
            Platform.runLater(() -> showError(e.getMessage()));
        }
    }).start();
}
```

#### 4. Payment Extensions Module

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/extensions` | Fetch all extension requests | LANDLORD |
| POST | `/api/v1/extensions/{id}/action` | Approve or reject extension | LANDLORD |

**Request Body for Approval:**
```json
{
  "action": "APPROVE"
}
```

**Request Body for Rejection:**
```json
{
  "action": "REJECT",
  "reason": "Insufficient justification"
}
```

**Example Implementation:**
```java
// In ExtensionController.java
private void handleApproveExtension(Extension extension) {
    if (!session.canApproveExtensions()) {
        showError("Access Denied: Only LANDLORD can approve extensions.");
        return;
    }
    
    new Thread(() -> {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("action", "APPROVE");
            
            RestClient.post("/extensions/" + extension.getId() + "/action", 
                          requestBody, String.class);
            
            Platform.runLater(() -> {
                showInfo("Extension approved successfully");
                loadExtensions();
            });
        } catch (Exception e) {
            Platform.runLater(() -> showError(e.getMessage()));
        }
    }).start();
}
```

#### 5. Payments Module

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/payments` | Fetch all M-Pesa payment records | LANDLORD |

**Example Implementation:**
```java
// In PaymentController.java
public void loadPayments() {
    new Thread(() -> {
        try {
            String response = RestClient.get("/payments", String.class);
            List<Payment> payments = RestClient.getGson().fromJson(
                response, 
                new TypeToken<List<Payment>>(){}.getType()
            );
            
            Platform.runLater(() -> {
                paymentsList.clear();
                paymentsList.addAll(payments);
                updateStats();
            });
        } catch (Exception e) {
            Platform.runLater(() -> showError(e.getMessage()));
        }
    }).start();
}
```

---

## 🛡️ Role-Based Access Control

### Implementation Strategy

#### 1. Navigation Restrictions (MainController)

```java
private void applyRoleBasedRestrictions() {
    if (session.isLandlord()) {
        // Disable CARETAKER modules
        tenantsBtn.setDisable(true);
        unitsBtn.setDisable(true);
        ticketsBtn.setDisable(true);
    } else if (session.isCaretaker()) {
        // Disable LANDLORD modules
        extensionsBtn.setDisable(true);
        paymentsBtn.setDisable(true);
    }
}
```

#### 2. Method-Level Access Control

```java
@FXML
private void showExtensions() {
    if (!session.canAccessExtensions()) {
        showAccessDenied("Payment Extensions");
        return;
    }
    loadView("views/extensions.fxml", "Payment Extensions", "...");
}
```

#### 3. UI Component Restrictions

```java
// In ExtensionController - Disable approve/reject buttons for non-landlords
@Override
protected void updateItem(Void item, boolean empty) {
    if (!empty) {
        Extension extension = getTableView().getItems().get(getIndex());
        boolean canApprove = session.canApproveExtensions();
        
        approveButton.setDisable(!extension.isPending() || !canApprove);
        rejectButton.setDisable(!extension.isPending() || !canApprove);
    }
}
```

#### 4. Dashboard Role-Based Rendering

```java
// In DashboardController
private void loadDashboardData() {
    if (session.isLandlord()) {
        loadLandlordDashboard();  // Revenue, extensions, collections
    } else if (session.isCaretaker()) {
        loadCaretakerDashboard(); // Units, tenants, tickets
    }
}
```

---

## ⚠️ Error Handling

### Exception Handling Pattern

```java
new Thread(() -> {
    try {
        // API call
        String response = RestClient.get("/endpoint", String.class);
        
        // Process response
        Platform.runLater(() -> {
            // Update UI on success
        });
        
    } catch (IOException e) {
        Platform.runLater(() -> {
            showError("Network error: " + e.getMessage());
            e.printStackTrace();
        });
    } catch (InterruptedException e) {
        Platform.runLater(() -> {
            showError("Request interrupted: " + e.getMessage());
        });
        Thread.currentThread().interrupt();
    } catch (Exception e) {
        Platform.runLater(() -> {
            showError("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        });
    }
}).start();
```

### HTTP Status Code Handling

The `RestClient` throws `IOException` for non-2xx status codes:

```java
if (response.statusCode() >= 200 && response.statusCode() < 300) {
    return gson.fromJson(response.body(), responseType);
} else {
    throw new IOException("HTTP Error: " + response.statusCode() + " - " + response.body());
}
```

### User-Friendly Error Messages

```java
private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
```

---

## 📈 Scaling Guidelines

### 1. Adding New Endpoints

**Step 1:** Define the model class
```java
public class NewEntity {
    private Long id;
    private String name;
    // Getters and setters
}
```

**Step 2:** Create API call in controller
```java
public void loadNewEntities() {
    new Thread(() -> {
        try {
            String response = RestClient.get("/new-endpoint", String.class);
            List<NewEntity> entities = RestClient.getGson().fromJson(
                response, 
                new TypeToken<List<NewEntity>>(){}.getType()
            );
            
            Platform.runLater(() -> updateUI(entities));
        } catch (Exception e) {
            Platform.runLater(() -> showError(e.getMessage()));
        }
    }).start();
}
```

### 2. Adding New Roles

**Step 1:** Update UserSession
```java
public boolean isManager() {
    return "MANAGER".equalsIgnoreCase(role);
}

public boolean canAccessReports() {
    return isManager() || isAdmin();
}
```

**Step 2:** Update MainController restrictions
```java
private void applyRoleBasedRestrictions() {
    if (session.isManager()) {
        // Configure manager-specific access
    }
}
```

### 3. Implementing Request/Response Interceptors

```java
// Add logging interceptor
private static HttpRequest.Builder addLoggingInterceptor(HttpRequest.Builder builder) {
    System.out.println("Request: " + builder.build().uri());
    return builder;
}
```

### 4. Caching Strategy

```java
// Simple in-memory cache
private static Map<String, CachedResponse> cache = new ConcurrentHashMap<>();

public static <T> T getCached(String endpoint, Class<T> responseType, Duration ttl) {
    CachedResponse cached = cache.get(endpoint);
    if (cached != null && !cached.isExpired(ttl)) {
        return (T) cached.getData();
    }
    
    T response = get(endpoint, responseType);
    cache.put(endpoint, new CachedResponse(response, LocalDateTime.now()));
    return response;
}
```

---

## 🧪 Testing

### Unit Testing RestClient

```java
@Test
public void testGetRequest() throws Exception {
    // Mock server setup
    MockWebServer server = new MockWebServer();
    server.enqueue(new MockResponse()
        .setBody("{\"id\":1,\"name\":\"Test\"}")
        .addHeader("Content-Type", "application/json"));
    server.start();
    
    // Test
    String response = RestClient.get("/test", String.class);
    assertNotNull(response);
    
    server.shutdown();
}
```

### Integration Testing

```java
@Test
public void testTenantControllerLoadTenants() {
    // Ensure backend is running on localhost:8080
    TenantController controller = new TenantController();
    controller.initialize();
    controller.loadTenants();
    
    // Wait for async operation
    Thread.sleep(2000);
    
    // Verify UI updated
    assertTrue(controller.getTenantsList().size() > 0);
}
```

---

## 🔧 Troubleshooting

### Common Issues

#### 1. Connection Refused
**Problem:** `java.net.ConnectException: Connection refused`

**Solution:**
- Verify Spring Boot backend is running on port 8080
- Check firewall settings
- Ensure BASE_URL is correct

#### 2. 401 Unauthorized
**Problem:** API returns 401 status code

**Solution:**
- Verify user is logged in: `UserSession.getInstance().isAuthenticated()`
- Check auth token is valid
- Ensure token is not expired

#### 3. JSON Parsing Errors
**Problem:** `com.google.gson.JsonSyntaxException`

**Solution:**
- Verify model class fields match API response
- Check date/time format configuration in Gson
- Use `TypeToken` for generic collections

#### 4. JavaFX Thread Issues
**Problem:** `IllegalStateException: Not on FX application thread`

**Solution:**
- Always wrap UI updates in `Platform.runLater()`
- Perform API calls in background threads
- Never block the JavaFX Application Thread

#### 5. CORS Issues (if applicable)
**Problem:** CORS policy blocking requests

**Solution:**
- Configure Spring Boot CORS settings
- Add `@CrossOrigin` annotation to controllers
- Verify allowed origins include frontend URL

---

## 📚 Additional Resources

### Dependencies (pom.xml)
```xml
<dependencies>
    <!-- JavaFX -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.8</version>
    </dependency>
    
    <!-- Gson for JSON -->
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```

### Best Practices

1. **Always use background threads for API calls**
2. **Update UI only on JavaFX Application Thread**
3. **Implement proper error handling and user feedback**
4. **Validate user permissions before API calls**
5. **Use meaningful variable and method names**
6. **Document complex business logic**
7. **Keep controllers focused on UI logic**
8. **Separate concerns: Network, Business Logic, UI**

---

## 📞 Support

For technical support or questions:
- **Email:** support@zonixrental.com
- **Documentation:** https://docs.zonixrental.com
- **Issue Tracker:** https://github.com/zonixrental/issues

---

**Document Version:** 1.0.0  
**Last Updated:** 2026-05-19  
**Author:** Bob - Elite JavaFX & Spring Boot Enterprise Architect

---

*Made with ❤️ by Bob*