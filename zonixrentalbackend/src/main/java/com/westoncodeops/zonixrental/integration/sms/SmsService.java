package com.westoncodeops.zonixrental.integration.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final TwilioProperties twilioProperties;

    @PostConstruct
    public void init() {
        if (twilioProperties.isEnabled() && twilioProperties.getAccountSid() != null && twilioProperties.getAuthToken() != null) {
            Twilio.init(twilioProperties.getAccountSid(), twilioProperties.getAuthToken());
            log.info("Twilio initialized successfully!");
        } else {
            log.warn("Twilio is disabled or not configured. SMS will be logged to console only.");
        }
    }

    public void sendWelcomeSms(String phoneNumber, String tenantName, String chatbotUrl) {
        String messageBody = String.format("""
            Hello %s! Welcome to Zonix Rental!
            
            Access your tenant portal here: %s
            
            Use this portal to:
            - Pay rent via M-Pesa
            - Request maintenance
            - Request payment extensions
            
            Thank you for choosing Zonix Rental!
            """, tenantName, chatbotUrl);

        sendSms(phoneNumber, messageBody);
    }

    public void sendPaymentConfirmationSms(String phoneNumber, String receiptNumber, double amount) {
        String messageBody = String.format("""
            Payment confirmed! Receipt: %s
            Amount: KSh %.2f
            
            Thank you for your payment!
            """, receiptNumber, amount);

        sendSms(phoneNumber, messageBody);
    }

    private void sendSms(String toPhoneNumber, String messageBody) {
        // Format phone number to E.164
        String formattedTo = formatPhoneNumber(toPhoneNumber);
        String fromPhoneNumber = formatPhoneNumber(twilioProperties.getPhoneNumber());

        if (twilioProperties.isEnabled() && twilioProperties.getAccountSid() != null && twilioProperties.getAuthToken() != null && fromPhoneNumber != null) {
            try {
                Message message = Message.creator(
                        new PhoneNumber(formattedTo),
                        new PhoneNumber(fromPhoneNumber),
                        messageBody
                ).create();

                log.info("SMS sent successfully! SID: {}", message.getSid());
                System.out.println("\n" + "=".repeat(60));
                System.out.println("📱 SMS SENT TO: " + formattedTo);
                System.out.println("-".repeat(60));
                System.out.println(messageBody);
                System.out.println("=".repeat(60) + "\n");
            } catch (Exception e) {
                log.error("Failed to send SMS via Twilio", e);
                log.info("Falling back to mock SMS logging");
                logMockSms(formattedTo, messageBody);
            }
        } else {
            logMockSms(formattedTo, messageBody);
        }
    }

    private void logMockSms(String toPhoneNumber, String messageBody) {
        log.info("=" .repeat(60));
        log.info("MOCK SMS TO: {}", toPhoneNumber);
        log.info("MESSAGE: {}", messageBody);
        log.info("=" .repeat(60));

        System.out.println("\n" + "=".repeat(60));
        System.out.println("📱 MOCK SMS TO: " + toPhoneNumber);
        System.out.println("-".repeat(60));
        System.out.println(messageBody);
        System.out.println("=".repeat(60) + "\n");
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }

        // Remove all non-digit characters
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");

        if (cleaned.startsWith("0")) {
            // Kenyan number starting with 0 -> convert to +254
            return "+254" + cleaned.substring(1);
        } else if (cleaned.startsWith("254")) {
            // Kenyan number starting with 254 -> add +
            return "+" + cleaned;
        } else if (cleaned.startsWith("+")) {
            // Already in E.164 format
            return cleaned;
        } else {
            // Assume it's a Kenyan number and add +254
            return "+254" + cleaned;
        }
    }
}
