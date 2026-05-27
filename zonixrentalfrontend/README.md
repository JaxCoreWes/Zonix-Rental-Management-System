# Zonix Rental - Property Management System (Frontend)

A production-grade JavaFX desktop application for managing rental properties, tenants, maintenance tickets, payment extensions, and M-Pesa transactions.

## 🏗️ Architecture Overview

This is a **JavaFX 21** application built with **Maven** and **Java 17+**, designed to communicate with a Spring Boot REST API backend running on `http://localhost:8080`.

### Technology Stack
- **JavaFX 21** - Modern UI framework
- **Java 17+** - Programming language
- **Maven** - Build and dependency management
- **Gson 2.10.1** - JSON serialization/deserialization
- **Java HttpClient** - Native HTTP communication

---

## 📁 Project Structure

```
zonixrentalfrontend/
├── src/main/java/com/westoncodeops/sample/
│   ├── Main.java                          # Application entry point
│   ├── controllers/                       # FXML Controllers
│   │   ├── MainController.java           # Main shell navigation controller
│   │   ├── TenantController.java         # Tenant management & Add Tenant modal
│   │   ├── UnitController.java           # Unit management & assignments
│   │   ├── TicketController.java         # Maintenance ticket management
│   │   ├── ExtensionController.java      # Payment extension approvals
│   │   └── PaymentController.java        # Payment tracking & M-Pesa records
│   ├── models/                            # Client-side domain models
│   │   ├── Tenant.java                   # Tenant/User entity
│   │   ├── Unit.java                     # Rental unit entity
│   │   ├── Ticket.java                   # Maintenance ticket entity
│   │   ├── Extension.java                # Payment extension request entity
│   │   └── Payment.java                  # Payment transaction entity
│   └── network/
│       └── RestClient.java               # Unified HTTP client utility
│
├── src/main/resources/com/westoncodeops/sample/
│   ├── main.fxml                         # Main application shell layout
│   ├── style.css                         # Corporate design system stylesheet
│   └── views/                            # Module-specific view layouts
│       ├── dashboard.fxml                # Dashboard with statistics
│       ├── tenants.fxml                  # Tenant list view
│       ├── add_tenant_modal.fxml         # Add tenant popup form
│       ├── units.fxml                    # Unit management view
│       ├── tickets.fxml                  # Maintenance tickets view
│       ├── extensions.fxml               # Payment extensions view
│       └── payments.fxml                 # Payment records view
│
└── pom.xml                               # Maven configuration
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17 or higher** installed
- **Maven 3.6+** installed
- **Spring Boot backend** running on `http://localhost:8080`

### Installation & Running

1. **Clone the repository** (if not already done):
   ```bash
   cd zonixrentalfrontend
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn javafx:run
   ```

   Or using Maven wrapper:
   ```bash
   ./mvnw javafx:run    # Linux/Mac
   mvnw.cmd javafx:run  # Windows
   ```

4. **Alternative: Run from IDE**
   - Open the project in IntelliJ IDEA or Eclipse
   - Run `Main.java` as a Java Application

---

## 🎨 Design System

### Color Palette
- **Primary Navy Blue**: `#1E3A8A` - Sidebar, primary buttons
- **Accent Sky Blue**: `#3B82F6` - Hover states, active navigation
- **Background Light Gray**: `#F3F4F6` - Main canvas background
- **Text Dark Slate**: `#1F2937` - Primary text color
- **Success Green**: `#10B981` - Success states, completed items
- **Warning Yellow**: `#F59E0B` - Warning states, pending items
- **Error Red**: `#EF4444` - Error states, failed items

### UI Components
- **Sidebar Navigation**: Fixed 250px width with deep navy background
- **Content Cards**: White background with subtle shadows and 8px border radius
- **Tables**: Alternating row colors with hover effects
- **Buttons**: Rounded corners (6px), drop shadows, hover animations
- **Forms**: Clean input fields with focus states and validation

---

## 📡 Backend API Integration

### Base URL
```
http://localhost:8080/api/v1
```

### API Endpoints Used

#### Users/Tenants
- `POST /users/register` - Register new tenant
- `GET /users` - Get all users/tenants
- `GET /users/{id}` - Get user by ID

#### Units
- `GET /units` - Get all units
- `POST /units` - Create new unit
- `PUT /units/{id}/assign` - Assign tenant to unit

#### Maintenance Tickets
- `GET /maintenance` - Get all tickets
- `POST /maintenance` - Create new ticket
- `PUT /maintenance/{id}/resolve` - Resolve ticket

#### Payment Extensions
- `GET /extensions` - Get all extension requests
- `POST /extensions` - Create extension request
- `PUT /extensions/{id}/approve` - Approve extension
- `PUT /extensions/{id}/reject` - Reject extension

#### Payments
- `GET /payments` - Get all payments
- `POST /payments` - Record new payment

### RestClient Usage Example

```java
// GET request
List<Tenant> tenants = RestClient.get("/users", new TypeToken<List<Tenant>>(){}.getType());

// POST request
Map<String, Object> requestData = new HashMap<>();
requestData.put("firstName", "John");
requestData.put("lastName", "Doe");
Tenant newTenant = RestClient.post("/users/register", requestData, Tenant.class);

// PUT request
RestClient.put("/extensions/1/approve", null, Void.class);

// DELETE request
RestClient.delete("/users/1");
```

---

## 🧩 Module Descriptions

### 1. Dashboard
- **Purpose**: Overview statistics and quick insights
- **Features**: 
  - Total tenants, units, tickets, extensions, payments
  - Visual cards with icons and color-coded stats
  - Real-time data display

### 2. Tenants
- **Purpose**: Manage tenant registrations and information
- **Features**:
  - TableView with search and filtering
  - Add Tenant modal with form validation
  - Role assignment (TENANT, CARETAKER, ADMIN)
  - Email, phone, and national ID tracking

### 3. Units
- **Purpose**: Track rental units and availability
- **Features**:
  - Unit status tracking (AVAILABLE, OCCUPIED)
  - Tenant assignment functionality
  - Rent amount display
  - Filter by availability status

### 4. Maintenance Tickets
- **Purpose**: Manage caretaker maintenance requests
- **Features**:
  - Ticket creation and tracking
  - Priority levels (HIGH, MEDIUM, LOW)
  - Status management (PENDING, IN_PROGRESS, RESOLVED)
  - Assignment to caretakers

### 5. Payment Extensions
- **Purpose**: Review and approve promise-to-pay requests
- **Features**:
  - Extension request listing
  - Approve/Reject actions with reasons
  - Promised payment date tracking
  - Amount due display

### 6. Payments
- **Purpose**: Track M-Pesa and other payment transactions
- **Features**:
  - Payment history with M-Pesa receipt numbers
  - Status tracking (COMPLETED, PENDING, FAILED)
  - Total amount calculations
  - Payment method filtering

---

## 🔧 Configuration

### Changing Backend URL
Edit `RestClient.java`:
```java
private static final String BASE_URL = "http://localhost:8080/api/v1";
```

### Customizing Colors
Edit `style.css` color variables:
```css
* {
    -fx-primary-navy: #1E3A8A;
    -fx-accent-blue: #3B82F6;
    /* ... other colors ... */
}
```

### Window Size
Edit `Main.java`:
```java
Scene scene = new Scene(root, 1400, 900);  // Width, Height
primaryStage.setMinWidth(1200);
primaryStage.setMinHeight(700);
```

---

## 🐛 Troubleshooting

### Application won't start
- Ensure Java 17+ is installed: `java -version`
- Verify Maven is installed: `mvn -version`
- Check if port 8080 is accessible (backend running)

### Views not loading
- Check FXML file paths in controllers
- Verify all FXML files are in `src/main/resources/com/westoncodeops/sample/views/`
- Check console for detailed error messages

### API connection errors
- Ensure backend is running on `http://localhost:8080`
- Check network connectivity
- Verify API endpoints match backend implementation
- Review RestClient error messages in console

### Styling issues
- Ensure `style.css` is properly linked in `main.fxml`
- Check CSS class names match between FXML and CSS
- Clear JavaFX cache if styles don't update

---

## 📝 Development Guidelines

### Adding a New View

1. **Create FXML file** in `src/main/resources/com/westoncodeops/sample/views/`
2. **Create Controller** in `src/main/java/com/westoncodeops/sample/controllers/`
3. **Link controller** in FXML: `fx:controller="com.westoncodeops.sample.controllers.YourController"`
4. **Add navigation** in `MainController.java`
5. **Add button** in `main.fxml` sidebar

### Adding a New Model

1. Create class in `models/` package
2. Add properties with getters/setters
3. Ensure proper JSON serialization compatibility
4. Add helper methods as needed

### Making API Calls

```java
new Thread(() -> {
    try {
        // API call
        String response = RestClient.get("/endpoint", String.class);
        
        Platform.runLater(() -> {
            // Update UI on JavaFX thread
        });
    } catch (Exception e) {
        Platform.runLater(() -> {
            // Show error
        });
    }
}).start();
```

---

## 🤝 Contributing

1. Follow existing code structure and naming conventions
2. Use meaningful variable and method names
3. Add JavaDoc comments for public methods
4. Test all UI interactions before committing
5. Ensure backend compatibility

---

## 📄 License

This project is part of the Zonix Rental Property Management System.

---

## 👥 Support

For issues or questions:
- Check the troubleshooting section above
- Review backend API documentation
- Contact: WestonCodeOps Development Team

---

## 🎯 Future Enhancements

- [ ] User authentication and session management
- [ ] Real-time notifications
- [ ] Report generation (PDF/Excel)
- [ ] Advanced search and filtering
- [ ] Data visualization charts
- [ ] Multi-language support
- [ ] Dark mode theme
- [ ] Offline mode with local caching

---

**Version**: 1.0.0  
**Last Updated**: 2026-05-18  
**Built with** ❤️ **by WestonCodeOps**
