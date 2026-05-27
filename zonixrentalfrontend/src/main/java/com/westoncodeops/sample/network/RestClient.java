package com.westoncodeops.sample.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.westoncodeops.sample.session.UserSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Unified REST client utility using Java's native HttpClient
 * Handles all HTTP communication with the Spring Boot backend
 * Automatically includes authentication tokens from UserSession
 */
public class RestClient {
    private static final String BASE_URL = "http://localhost:8080/api/v1";
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, context) ->
                    LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (date, type, context) ->
                    new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, context) ->
                    LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (dateTime, type, context) ->
                    new JsonPrimitive(dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(BigDecimal.class, (JsonDeserializer<BigDecimal>) (json, type, context) ->
                    new BigDecimal(json.getAsString()))
            .registerTypeAdapter(BigDecimal.class, (JsonSerializer<BigDecimal>) (value, type, context) ->
                    new JsonPrimitive(value.toPlainString()))
            .create();

    /**
     * Build HTTP request with authentication headers
     * @param builder The HttpRequest.Builder to add headers to
     * @return The builder with authentication headers added
     */
    private static HttpRequest.Builder addAuthHeaders(HttpRequest.Builder builder) {
        builder.header("Content-Type", "application/json");
        
        // Add authentication token if user is logged in
        UserSession session = UserSession.getInstance();
        if (session.isAuthenticated() && session.getAuthToken() != null) {
            builder.header("Authorization", "Bearer " + session.getAuthToken());
        }
        
        return builder;
    }

    /**
     * Perform a GET request
     * @param endpoint The API endpoint (e.g., "/users")
     * @param responseType The class type to deserialize the response into
     * @return The deserialized response object
     */
    public static <T> T get(String endpoint, Class<T> responseType) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .GET();
        
        HttpRequest request = addAuthHeaders(builder).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            if (responseType == String.class) {
                return (T) response.body();
            }
            return gson.fromJson(response.body(), responseType);
        } else {
            throw new IOException("HTTP Error: " + response.statusCode() + " - " + response.body());
        }
    }

    /**
     * Perform a POST request
     * @param endpoint The API endpoint (e.g., "/users/register")
     * @param requestBody The request body object to serialize
     * @param responseType The class type to deserialize the response into
     * @return The deserialized response object
     */
    public static <T> T post(String endpoint, Object requestBody, Class<T> responseType)
            throws IOException, InterruptedException {
        String jsonBody = gson.toJson(requestBody);
        
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
        
        HttpRequest request = addAuthHeaders(builder).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            if (responseType == Void.class || response.body().isEmpty()) {
                return null;
            }
            if (responseType == String.class) {
                return (T) response.body();
            }
            return gson.fromJson(response.body(), responseType);
        } else {
            throw new IOException("HTTP Error: " + response.statusCode() + " - " + response.body());
        }
    }

    /**
     * Perform a PUT request
     * @param endpoint The API endpoint (e.g., "/users/1")
     * @param requestBody The request body object to serialize
     * @param responseType The class type to deserialize the response into
     * @return The deserialized response object
     */
    public static <T> T put(String endpoint, Object requestBody, Class<T> responseType)
            throws IOException, InterruptedException {
        String jsonBody = gson.toJson(requestBody);
        
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
        
        HttpRequest request = addAuthHeaders(builder).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            if (responseType == Void.class || response.body().isEmpty()) {
                return null;
            }
            if (responseType == String.class) {
                return (T) response.body();
            }
            return gson.fromJson(response.body(), responseType);
        } else {
            throw new IOException("HTTP Error: " + response.statusCode() + " - " + response.body());
        }
    }

    /**
     * Perform a DELETE request
     * @param endpoint The API endpoint (e.g., "/users/1")
     */
    public static void delete(String endpoint) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .DELETE();
        
        HttpRequest request = addAuthHeaders(builder).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("HTTP Error: " + response.statusCode() + " - " + response.body());
        }
    }

    /**
     * Get the Gson instance for custom serialization/deserialization
     */
    public static Gson getGson() {
        return gson;
    }
}

// Made with Bob
