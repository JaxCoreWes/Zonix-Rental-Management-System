package com.westoncodeops.zonixrental.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westoncodeops.zonixrental.DTOs.Requests.PaymentRequest;
import com.westoncodeops.zonixrental.entities.Payment;
import com.westoncodeops.zonixrental.entities.Unit;
import com.westoncodeops.zonixrental.entities.User;
import com.westoncodeops.zonixrental.enums.PaymentStatus;
import com.westoncodeops.zonixrental.integration.daraja.DarajaService;
import com.westoncodeops.zonixrental.repository.PaymentRepository;
import com.westoncodeops.zonixrental.repository.UnitRepository;
import com.westoncodeops.zonixrental.repository.UserRepository;
import com.westoncodeops.zonixrental.service.PaymentService.IPaymentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/daraja")
@RequiredArgsConstructor
public class DarajaController {

    private final DarajaService darajaService;
    private final IPaymentService paymentService;
    private final UserRepository userRepository;
    private final UnitRepository unitRepository;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // In-memory store to map checkoutRequestId to transaction details
    private final Map<String, PendingPayment> pendingPayments = new ConcurrentHashMap<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PendingPayment {
        private String tenantPhoneNumber;
        private Long unitId;
        private double amount;
    }

    @PostMapping("/stkpush")
    public ResponseEntity<Map<String, Object>> initiateStkPush(@RequestBody StkPushRequest request) {
        try {
            String response = darajaService.initiateStkPush(
                    request.phoneNumber(),
                    request.amount(),
                    request.accountReference(),
                    request.transactionDesc()
            );

            JsonNode jsonResponse = objectMapper.readTree(response);
            String checkoutRequestId = jsonResponse.path("CheckoutRequestID").asText();
            
            // Store pending payment details
            if (request.unitId() != null) {
                pendingPayments.put(checkoutRequestId, 
                    new PendingPayment(request.phoneNumber(), request.unitId(), request.amount()));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("merchantRequestId", jsonResponse.path("MerchantRequestID").asText());
            result.put("checkoutRequestId", checkoutRequestId);
            result.put("responseCode", jsonResponse.path("ResponseCode").asText());
            result.put("responseDescription", jsonResponse.path("ResponseDescription").asText());
            result.put("customerMessage", jsonResponse.path("CustomerMessage").asText());

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            log.error("Error initiating STK Push", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to initiate payment: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody String callbackBody) {
        try {
            log.info("Received Daraja callback: {}", callbackBody);
            JsonNode jsonNode = objectMapper.readTree(callbackBody);
            JsonNode stkCallback = jsonNode.path("Body").path("stkCallback");

            String merchantRequestId = stkCallback.path("MerchantRequestID").asText();
            String checkoutRequestId = stkCallback.path("CheckoutRequestID").asText();
            int resultCode = stkCallback.path("ResultCode").asInt();
            String resultDesc = stkCallback.path("ResultDesc").asText();

            if (resultCode == 0) {
                // Payment successful
                JsonNode callbackMetadata = stkCallback.path("CallbackMetadata").path("Item");
                String mpesaReceiptNumber = "";
                double amount = 0;
                String phoneNumber = "";

                for (JsonNode item : callbackMetadata) {
                    String name = item.path("Name").asText();
                    switch (name) {
                        case "MpesaReceiptNumber" -> mpesaReceiptNumber = item.path("Value").asText();
                        case "Amount" -> amount = item.path("Value").asDouble();
                        case "PhoneNumber" -> phoneNumber = item.path("Value").asText();
                    }
                }

                log.info("Payment successful! Receipt: {}, Amount: {}, Phone: {}", 
                        mpesaReceiptNumber, amount, phoneNumber);

                // Check if we have pending payment details
                PendingPayment pending = pendingPayments.get(checkoutRequestId);
                if (pending != null) {
                    saveSuccessfulPayment(pending, mpesaReceiptNumber);
                    pendingPayments.remove(checkoutRequestId);
                }
            } else {
                log.warn("Payment failed: {}", resultDesc);
                pendingPayments.remove(checkoutRequestId);
            }

            return ResponseEntity.ok("Callback received successfully");
        } catch (Exception e) {
            log.error("Error processing callback", e);
            return ResponseEntity.badRequest().body("Error processing callback");
        }
    }

    private void saveSuccessfulPayment(PendingPayment pending, String mpesaReceiptNumber) {
        try {
            Optional<User> tenantOpt = userRepository.findByPhoneNumber(normalizePhoneNumber(pending.getTenantPhoneNumber()));
            Optional<Unit> unitOpt = unitRepository.findById(pending.getUnitId());

            if (tenantOpt.isPresent() && unitOpt.isPresent()) {
                User tenant = tenantOpt.get();
                Unit unit = unitOpt.get();

                Payment payment = Payment.builder()
                        .amount(BigDecimal.valueOf(pending.getAmount()))
                        .mpesaRef(mpesaReceiptNumber)
                        .status(PaymentStatus.PAID)
                        .paymentDate(LocalDateTime.now())
                        .tenant(tenant)
                        .unit(unit)
                        .payer(normalizePhoneNumber(pending.getTenantPhoneNumber()))
                        .coversFrom(LocalDate.now())
                        .coversUntil(LocalDate.now().plusMonths(1))
                        .build();

                paymentRepository.save(payment);
                log.info("Payment saved successfully to database! Receipt: {}", mpesaReceiptNumber);
            } else {
                log.warn("Could not find tenant or unit for pending payment");
            }
        } catch (Exception e) {
            log.error("Error saving payment to database", e);
        }
    }

    private String normalizePhoneNumber(String phone) {
        if (phone.startsWith("+")) phone = phone.substring(1);
        if (phone.startsWith("0")) phone = "254" + phone.substring(1);
        if (!phone.startsWith("254")) phone = "254" + phone;
        return phone;
    }

    @GetMapping("/status/{checkoutRequestId}")
    public ResponseEntity<Map<String, Object>> checkTransactionStatus(@PathVariable String checkoutRequestId) {
        try {
            String response = darajaService.checkTransactionStatus(checkoutRequestId);
            JsonNode jsonResponse = objectMapper.readTree(response);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", jsonResponse);

            // Check if result code is 0 and we have pending payment
            if (jsonResponse.has("ResultCode") && jsonResponse.get("ResultCode").asInt() == 0) {
                PendingPayment pending = pendingPayments.get(checkoutRequestId);
                if (pending != null) {
                    // Extract receipt number from response if available
                    String receipt = "";
                    if (jsonResponse.has("CallbackMetadata")) {
                        JsonNode items = jsonResponse.path("CallbackMetadata").path("Item");
                        for (JsonNode item : items) {
                            if ("MpesaReceiptNumber".equals(item.path("Name").asText())) {
                                receipt = item.path("Value").asText();
                            }
                        }
                    }
                    if (!receipt.isEmpty()) {
                        saveSuccessfulPayment(pending, receipt);
                        pendingPayments.remove(checkoutRequestId);
                    }
                }
            }

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            log.error("Error checking transaction status", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Failed to check transaction status: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    public record StkPushRequest(
            String phoneNumber,
            double amount,
            String accountReference,
            String transactionDesc,
            Long unitId
    ) {}
}
