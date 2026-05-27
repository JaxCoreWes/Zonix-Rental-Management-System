# Zonix Rental - Property Management System (Backend)

A production-grade Spring Boot REST API for managing rental properties, tenants, maintenance tickets, payment extensions, and M-Pesa transactions.

## 🏗️ Architecture Overview

This is a **Spring Boot 3.x** application built with **Maven** and **Java 17+**, providing RESTful APIs for the JavaFX frontend application.

### Technology Stack
- **Spring Boot 3.x** - Application framework
- **Spring Data JPA** - Data persistence
- **MySQL/PostgreSQL** - Database (configurable)
- **Java 17+** - Programming language
- **Maven** - Build and dependency management
- **Lombok** - Boilerplate code reduction

---

## 📁 Project Structure

```
zonixrentalbackend/
├── src/main/java/com/westoncodeops/zonixrental/
│   ├── ZonixrentalApplication.java        # Application entry point
│   ├── controllers/                       # REST API Controllers
│   │   ├── UserController.java           # User/Tenant management endpoints
│   │   ├── UnitController.java           # Unit management endpoints
│   │   ├── MaintenanceController.java    # Maintenance ticket endpoints
│   │   ├── ExtensionController.java      # Payment extension endpoints
│   │   ├── PaymentController.java        # Payment transaction endpoints
│   │   └── CaretakerController.java      # Caretaker-specific endpoints
│   ├── entities/                          # JPA Entity models
│   │   ├── User.java                     # User/Tenant entity
│   │   ├── Unit.java                     # Rental unit entity
│   │   ├── MaintenanceTicket.java        # Maintenance ticket entity
│   │   ├── ExtensionRequest.java         # Payment extension entity
│   │   └── Payment.java                  # Payment transaction entity
│   ├── repository/                        # Spring Data JPA Repositories
│   │   ├── UserRepository.java
│   │   ├── UnitRepository.java
│   │   ├── MaintenanceRepository.java
│   │   ├── ExtensionRepository.java
│   │   └── PaymentRepository.java
│   ├── service/                           # Business logic services
│   │   ├── UserService/
│   │   ├── UnitService/
│   │   ├── MaintenanceService/
│   │   ├── ExtensionService/
│   │   ├── PaymentService/
│   │   └── CaretakerUIService/
│   ├── DTOs/                              # Data Transfer Objects
│   │   ├── Requests/                     # Request DTOs
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── CreateUnitRequest.java
│   │   │   ├── CreateTicketRequest.java
│   │   │   ├── CreateExtensionRequest.java
│   │   │   └── PaymentRequest.java
│   │   └── Responses/                    # Response DTOs
│   │       ├── UserResponse.java
│   │       ├── UnitResponse.java
│   │       ├── MaintenanceTicketResponse.java
│   │       ├── ExtensionResponse.java
│   │       └── PaymentResponse.java
│   ├── enums/                             # Enumeration types
│   │   ├── Role.java                     # User roles
│   │   ├── MaintenanceCategory.java      # Ticket categories
│   │   ├── TicketStatus.java             # Ticket statuses
│   │   ├── ExtensionStatus.java          # Extension statuses
│   │   └── PaymentStatus.java            # Payment statuses
│   └── exceptions/                        # Custom exceptions
│       └── ResourceNotFoundException.java
│
├── src/main/resources/
│   └── application.properties            # Application configuration
│
└── pom.xml                               # Maven configuration
```

---

## 🚀 Getting Started

### Prerequisites
- **Java 17 or higher** installed
- **Maven 3.6+** installed
- **MySQL** or **PostgreSQL** database server running

### Database Setup

1. **Create database**:
   ```sql
   CREATE DATABASE zonixrental;
   ```

2. **Configure database connection** in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/zonixrental
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

### Installation & Running

1. **Navigate to backend directory**:
   ```bash
   cd zonixrentalbackend
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

   Or using Maven wrapper:
   ```bash
   ./mvnw spring-boot:run    # Linux/Mac
   mvnw.cmd spring-boot:run  # Windows
   ```

4. **Alternative: Run from IDE**
   - Open the project in IntelliJ IDEA or Eclipse
   - Run `ZonixrentalApplication.java` as a Java Application

5. **Verify the application is running**:
   - Open browser: `http://localhost:8080`
   - API base URL: `http://localhost:8080/api/v1`

---

## 📡 API Endpoints

### Base URL
```
http://localhost:8080/api/v1
```

### 1. User/Tenant Management

#### Register New User
```http
POST /users/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "254712345678",
  "nationalId": "12345678",
  "password": "securePassword123",
  "role": "TENANT"
}
```

#### Get All Users
```http
GET /users
```

#### Get User by ID
```http
GET /users/{id}
```

#### Update User
```http
PUT /users/{id}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "254712345678"
}
```

#### Delete User
```http
DELETE /users/{id}
```

---

### 2. Unit Management

#### Create New Unit
```http
POST /units
Content-Type: application/json

{
  "unitNumber": "A101",
  "unitType": "2 Bedroom",
  "rentAmount": 25000.00
}
```

#### Get All Units
```http
GET /units
```

#### Get Unit by ID
```http
GET /units/{id}
```

#### Assign Tenant to Unit
```http
PUT /units/{unitId}/assign
Content-Type: application/json

{
  "tenantId": 1
}
```

#### Get Available Units
```http
GET /units/available
```

---

### 3. Maintenance Tickets

#### Create Maintenance Ticket
```http
POST /maintenance
Content-Type: application/json

{
  "title": "Leaking Faucet",
  "description": "Kitchen faucet is leaking",
  "category": "PLUMBING",
  "priority": "MEDIUM",
  "unitId": 1,
  "reportedById": 2
}
```

#### Get All Tickets
```http
GET /maintenance
```

#### Get Ticket by ID
```http
GET /maintenance/{id}
```

#### Assign Ticket to Caretaker
```http
PUT /maintenance/{id}/assign/{caretakerId}
```

#### Resolve Ticket
```http
PUT /maintenance/{id}/resolve
```

#### Get Tickets by Status
```http
GET /maintenance/status/{status}
```

---

### 4. Payment Extensions

#### Create Extension Request
```http
POST /extensions
Content-Type: application/json

{
  "tenantId": 1,
  "unitId": 1,
  "promisedPaymentDate": "2026-06-15",
  "amountDue": 25000.00,
  "reason": "Waiting for salary"
}
```

#### Get All Extensions
```http
GET /extensions
```

#### Get Extension by ID
```http
GET /extensions/{id}
```

#### Approve Extension
```http
PUT /extensions/{id}/approve
Content-Type: application/json

{
  "approvedById": 3
}
```

#### Reject Extension
```http
PUT /extensions/{id}/reject
Content-Type: application/json

{
  "rejectionReason": "Insufficient justification",
  "rejectedById": 3
}
```

#### Get Pending Extensions
```http
GET /extensions/pending
```

---

### 5. Payments

#### Record Payment
```http
POST /payments
Content-Type: application/json

{
  "tenantId": 1,
  "unitId": 1,
  "amount": 25000.00,
  "paymentMethod": "MPESA",
  "mpesaReceiptNumber": "QGH7XYZ123",
  "phoneNumber": "254712345678",
  "paymentFor": "RENT"
}
```

#### Get All Payments
```http
GET /payments
```

#### Get Payment by ID
```http
GET /payments/{id}
```

#### Get Payments by Tenant
```http
GET /payments/tenant/{tenantId}
```

#### Get Payments by Unit
```http
GET /payments/unit/{unitId}
```

#### Get Payments by Status
```http
GET /payments/status/{status}
```

---

### 6. Caretaker UI Endpoints

#### Get Caretaker Dashboard Data
```http
GET /caretaker/units
```

---

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    national_id VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Units Table
```sql
CREATE TABLE units (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    unit_number VARCHAR(50) UNIQUE NOT NULL,
    unit_type VARCHAR(100) NOT NULL,
    rent_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    tenant_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES users(id)
);
```

### Maintenance Tickets Table
```sql
CREATE TABLE maintenance_tickets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    unit_id BIGINT NOT NULL,
    reported_by_id BIGINT NOT NULL,
    assigned_to_id BIGINT,
    reported_date DATE NOT NULL,
    resolved_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (unit_id) REFERENCES units(id),
    FOREIGN KEY (reported_by_id) REFERENCES users(id),
    FOREIGN KEY (assigned_to_id) REFERENCES users(id)
);
```

### Extension Requests Table
```sql
CREATE TABLE extension_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,
    requested_date DATE NOT NULL,
    promised_payment_date DATE NOT NULL,
    amount_due DECIMAL(10,2) NOT NULL,
    reason TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',
    approved_by_id BIGINT,
    approved_date DATE,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES users(id),
    FOREIGN KEY (unit_id) REFERENCES units(id),
    FOREIGN KEY (approved_by_id) REFERENCES users(id)
);
```

### Payments Table
```sql
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tenant_id BIGINT NOT NULL,
    unit_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_reference VARCHAR(255),
    mpesa_receipt_number VARCHAR(100),
    phone_number VARCHAR(20),
    status VARCHAR(20) DEFAULT 'PENDING',
    payment_date TIMESTAMP NOT NULL,
    payment_for VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES users(id),
    FOREIGN KEY (unit_id) REFERENCES units(id)
);
```

---

## 🔧 Configuration

### Application Properties

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/zonixrental
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Logging Configuration
logging.level.com.westoncodeops.zonixrental=DEBUG
logging.level.org.springframework.web=INFO
```

---

## 🔐 Security Considerations

### Current Implementation
- Basic password storage (should be encrypted in production)
- No authentication/authorization implemented yet

### Recommended Enhancements
1. **Spring Security** - Add authentication and authorization
2. **JWT Tokens** - Implement token-based authentication
3. **Password Encryption** - Use BCrypt for password hashing
4. **CORS Configuration** - Configure allowed origins
5. **Rate Limiting** - Prevent API abuse
6. **Input Validation** - Add comprehensive validation

---

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
```bash
mvn clean test jacoco:report
```

---

## 📊 Monitoring & Logging

### Actuator Endpoints
Add Spring Boot Actuator for monitoring:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Access endpoints:
- Health: `http://localhost:8080/actuator/health`
- Metrics: `http://localhost:8080/actuator/metrics`

---

## 🐛 Troubleshooting

### Application won't start
- Check database connection settings
- Verify database server is running
- Check port 8080 is not in use
- Review application logs

### Database connection errors
- Verify database credentials
- Check database server is accessible
- Ensure database exists
- Check firewall settings

### API returns 404
- Verify endpoint URL is correct
- Check controller mappings
- Review application logs

---

## 🚀 Deployment

### Production Checklist
- [ ] Configure production database
- [ ] Enable HTTPS/SSL
- [ ] Implement authentication/authorization
- [ ] Set up logging and monitoring
- [ ] Configure CORS properly
- [ ] Enable rate limiting
- [ ] Set up backup strategy
- [ ] Configure environment variables
- [ ] Optimize database queries
- [ ] Enable caching where appropriate

### Build for Production
```bash
mvn clean package -DskipTests
```

The JAR file will be in `target/zonixrental-*.jar`

### Run Production JAR
```bash
java -jar target/zonixrental-1.0.0-SNAPSHOT.jar
```

---

## 🤝 Contributing

1. Follow Spring Boot best practices
2. Use proper HTTP status codes
3. Implement proper error handling
4. Add comprehensive logging
5. Write unit and integration tests
6. Document all API endpoints

---

## 📄 License

This project is part of the Zonix Rental Property Management System.

---

## 👥 Support

For issues or questions:
- Check the troubleshooting section above
- Review Spring Boot documentation
- Contact: WestonCodeOps Development Team

---

**Version**: 1.0.0  
**Last Updated**: 2026-05-18  
**Built with** ❤️ **by WestonCodeOps**