# Zonix Rental System - Presentation Guide

## 🎯 Quick Demo Steps for Tomorrow

### 1. Start the Backend
```bash
cd zonixrentalbackend
mvn spring-boot:run
```

### 2. Start the Frontend (Optional)
```bash
cd zonixrentalfrontend
mvn javafx:run
```

### 3. Open the Tenant Portal
Go to: http://localhost:8080/tenant-portal
Log in with: `0722222222`

---

## 📱 M-Pesa (Daraja) Setup

### Step 1: Get ngrok for public callback URL
- Download ngrok: https://ngrok.com/
- Run: `ngrok http 8080`
- Copy the HTTPS URL (e.g., `https://abc123.ngrok.io`)

### Step 2: Update Daraja Config
Edit `zonixrentalbackend/src/main/resources/application.properties`:
```properties
# Replace with your ngrok HTTPS URL
daraja.callback-url=https://YOUR-NGROK-URL.ngrok.io/api/v1/daraja/callback
```

### Step 3: Daraja Credentials (Already in Place!)
The following are already configured for Sandbox:
- Consumer Key: `oSBAk8EE8tylE2fv0SY6VXA047p33ApdD1zpA2JxPdfFGsC1`
- Consumer Secret: `E5wMWfk5Lp1BgmkMVIM0Si6YE7kuQLGvnKUZHsYuBobPPv9SPN06YbJc37zDvkuR`
- Business Short Code (Lipa Na Mpesa Sandbox): `174379`
- Pass Key: `bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e627edbccb3c29a`

### Step 4: Test M-Pesa Payment
1. Open Tenant Portal at http://localhost:8080/tenant-portal
2. Log in with phone number: `0722222222`
3. Go to "Pay Rent" tab
4. Enter your **real** M-Pesa phone number (e.g., `07XXXXXXXX`)
5. Enter amount (minimum 1 KSh for sandbox)
6. Click "Pay via M-Pesa"
7. You'll get an STK Push on your phone! Enter PIN to complete the payment.

---

## 💬 SMS Setup (Twilio)
- Twilio credentials are already configured
- When a tenant is registered, they will receive a welcome SMS (if `twilio.enabled=true`)

---

## 📊 System Features to Show
1. **Landlord/Caretaker Dashboard** (JavaFX app)
   - Add/Manage Units
   - Add Tenants
   - View Maintenance Tickets
   - Process Rent Extensions
2. **Tenant Portal** (Web UI)
   - Pay Rent via M-Pesa (STK Push)
   - Request Maintenance
   - Request Rent Extensions
   - View Payment History
3. **Backend API** - RESTful APIs for all operations

---

## 🔑 Demo Credentials
- **Landlord**: landlord@zonix.com / landlord123
- **Caretaker**: caretaker@zonix.com / caretaker123
- **Test Tenant Phone**: 0722222222

---

## 🎉 Presentation Flow Suggestion
1. Show the JavaFX dashboard (landlord view)
2. Add a new tenant using your **real phone number** to get SMS
3. Switch to Tenant Portal and log in with your number
4. Demo an M-Pesa payment (show the STK Push)
5. Show a maintenance request and rent extension
6. Show the dashboard updating with new data
