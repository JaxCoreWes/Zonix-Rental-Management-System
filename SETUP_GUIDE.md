# Zonix Rental - Setup and Testing Guide

## 1. Daraja M-Pesa Setup

1. Go to [Safaricom Developer Portal](https://developer.safaricom.co.ke/)
2. Login or create an account
3. Create a new app (name it "Zonix Rental" or similar)
4. Copy your **Consumer Key** and **Consumer Secret**
5. Open `zonixrentalbackend/src/main/resources/application.properties` and replace:
   - `YOUR_CONSUMER_KEY_HERE` with your Consumer Key
   - `YOUR_CONSUMER_SECRET_HERE` with your Consumer Secret

## 2. Twilio SMS Setup (Sending Real SMS!

1. Go to your [Twilio Console](https://console.twilio.com/)
2. Copy your:
   - **ACCOUNT SID
   - **AUTH TOKEN
3. Get a Twilio phone number from Twilio (trial numbers)
4. Verify your personal phone number in Twilio (required for free trial)
5. Open `zonixrentalbackend/src/main/resources/application.properties` and replace:
   - `YOUR_TWILIO_ACCOUNT_SID` with your Account SID
   - `YOUR_TWILIO_AUTH_TOKEN` with your Auth Token
   - `YOUR_TWILIO_PHONE_NUMBER` with your Twilio number (include the +, e.g., +1234567890)
6. Make sure `twilio.enabled=true`

## 3. Testing with Real Phone Numbers

The system is already set up to work with real Kenyan phone numbers!

### To Test SMS:
When a new tenant is registered, they'll receive an SMS with the tenant portal link!
To test this manually, register a test tenant via the API or frontend.

### To Test M-Pesa Payments:
1. Open the tenant portal at http://localhost:8080/tenant-portal
2. Login with test phone number: `0722222222`
3. Go to "Pay Rent" tab
4. Enter your real M-Pesa phone number in the format: `2547XXXXXXXX`
5. Enter amount (minimum 1 KSh for testing)
6. Click "Pay via M-Pesa"
7. You should receive an STK push on your phone!

## 4. Ngrok for Public Callback URL (for Daraja)

To receive payment callbacks from Safaricom, you need a public URL:
1. Download [Ngrok](https://ngrok.com/)
2. Run: `ngrok http 8080`
3. Copy the HTTPS URL it gives you (e.g., `https://abc123.ngrok-free.app`)
4. Update `application.properties`:
   - `daraja.callback-url=https://abc123.ngrok-free.app/api/v1/daraja/callback`
5. Restart the backend

## 5. Test Credentials

- **Test Landlord Email:** landlord@zonix.com
- **Test Caretaker Email:** caretaker@zonix.com
- **Test Tenant Phone:** 0722222222
