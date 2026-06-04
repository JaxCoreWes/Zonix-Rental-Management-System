package com.westoncodeops.zonixrental.integration.daraja;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class DarajaService {

    private final DarajaProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OkHttpClient httpClient = new OkHttpClient();
    private String accessToken;
    private LocalDateTime tokenExpiry;

    private String getBaseUrl() {
        return "sandbox".equals(properties.getEnvironment()) 
                ? "https://sandbox.safaricom.co.ke" 
                : "https://api.safaricom.co.ke";
    }

    public String getAccessToken() throws IOException {
        if (accessToken != null && tokenExpiry != null && LocalDateTime.now().isBefore(tokenExpiry)) {
            log.info("Reusing existing access token");
            return accessToken;
        }

        String key = properties.getConsumerKey() != null ? properties.getConsumerKey() : "null";
        String secret = properties.getConsumerSecret() != null ? properties.getConsumerSecret() : "null";
        log.info("=== Daraja Credentials ===");
        log.info("Consumer Key: {}", key.length() > 4 ? key.substring(0, 4) + "..." : key);
        log.info("Consumer Secret: {}", secret.length() > 4 ? secret.substring(0, 4) + "..." : secret);
        log.info("Environment: {}", properties.getEnvironment());

        String auth = key + ":" + secret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        log.info("Encoded Auth (first 20): {}", encodedAuth.length() > 20 ? encodedAuth.substring(0, 20) + "..." : encodedAuth);

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/oauth/v1/generate?grant_type=client_credentials")
                .get()
                .addHeader("Authorization", "Basic " + encodedAuth)
                .build();

        log.info("=== Getting Access Token ===");
        log.info("URL: {}", request.url());

        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "no body";
            log.info("Response Code: {}", response.code());
            log.info("Response Body: {}", body);
            
            if (!response.isSuccessful()) {
                throw new IOException("Failed to get access token: " + response.code() + " - " + body);
            }

            JsonNode jsonNode = objectMapper.readTree(body);
            accessToken = jsonNode.get("access_token").asText();
            int expiresIn = jsonNode.get("expires_in").asInt();
            tokenExpiry = LocalDateTime.now().plusSeconds(expiresIn - 60); // Subtract 60s for buffer
            log.info("Got access token! Expires in {} seconds", expiresIn);
            return accessToken;
        }
    }

    public String initiateStkPush(String phoneNumber, double amount, String accountReference, String transactionDesc) throws IOException {
        String token = getAccessToken();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String password = Base64.getEncoder().encodeToString(
                (properties.getBusinessShortCode() + properties.getPassKey() + timestamp).getBytes(StandardCharsets.UTF_8)
        );

        // Format phone number
        if (phoneNumber.startsWith("+")) {
            phoneNumber = phoneNumber.substring(1);
        }
        if (phoneNumber.startsWith("0")) {
            phoneNumber = "254" + phoneNumber.substring(1);
        } else if (!phoneNumber.startsWith("254")) {
            phoneNumber = "254" + phoneNumber;
        }

        // Ensure amount is integer (Daraja requires whole numbers in sandbox)
        long amountLong = (long) Math.ceil(amount);
        if (amountLong < 1) amountLong = 1;

        String requestBody = String.format("""
                {
                    "BusinessShortCode": "%s",
                    "Password": "%s",
                    "Timestamp": "%s",
                    "TransactionType": "CustomerPayBillOnline",
                    "Amount": "%d",
                    "PartyA": "%s",
                    "PartyB": "%s",
                    "PhoneNumber": "%s",
                    "CallBackURL": "%s",
                    "AccountReference": "%s",
                    "TransactionDesc": "%s"
                }
                """,
                properties.getBusinessShortCode(),
                password,
                timestamp,
                amountLong,
                phoneNumber,
                properties.getBusinessShortCode(),
                phoneNumber,
                properties.getCallbackUrl(),
                accountReference,
                transactionDesc
        );
        // Log request details for debugging (do not log secrets in production)
        log.info("===== STK PUSH REQUEST =====");
        log.info("URL: {}", getBaseUrl() + "/mpesa/stkpush/v1/processrequest");
        log.info("Callback URL: {}", properties.getCallbackUrl());
        log.info("Token: {}", token.substring(0, Math.min(20, token.length())) + "...");
        log.info("Request Body: {}", requestBody);
        log.info("===========================");

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/mpesa/stkpush/v1/processrequest")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String respBody = response.body() != null ? response.body().string() : "";
            log.info("===== STK PUSH RESPONSE =====");
            log.info("HTTP Status: {}", response.code());
            log.info("Response Body: {}", respBody);
            log.info("============================");
            
            if (!response.isSuccessful()) {
                throw new IOException("Failed to initiate STK Push: HTTP " + response.code() + " - " + respBody);
            }
            
            return respBody;
        }
    }

    public String checkTransactionStatus(String checkoutRequestId) throws IOException {
        String token = getAccessToken();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String password = Base64.getEncoder().encodeToString(
                (properties.getBusinessShortCode() + properties.getPassKey() + timestamp).getBytes(StandardCharsets.UTF_8)
        );

        String requestBody = String.format("""
            {
                "BusinessShortCode": "%s",
                "Password": "%s",
                "Timestamp": "%s",
                "CheckoutRequestID": "%s"
            }
            """,
                properties.getBusinessShortCode(),
                password,
                timestamp,
                checkoutRequestId
        );

        Request request = new Request.Builder()
                .url(getBaseUrl() + "/mpesa/stkpushquery/v1/query")
                .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to check transaction status: " + response);
            }
            return response.body().string();
        }
    }
}
