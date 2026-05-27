package com.westoncodeops.sample.controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.westoncodeops.sample.network.RestClient;
import com.westoncodeops.sample.session.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML private TextField identifierField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        if (statusLabel != null) statusLabel.setText("");
    }

    @FXML
    public void handleLogin() {
        String identifier = identifierField.getText().trim();
        String password = passwordField.getText();

        if (identifier.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter identifier and password");
            return;
        }

        loginBtn.setDisable(true);
        statusLabel.setText("Signing in...");

        new Thread(() -> {
            try {
                Map<String, String> payload = new HashMap<>();
                payload.put("identifier", identifier);
                payload.put("password", password);

                // Request the backend login endpoint and parse JSON response
                JsonObject json = RestClient.post("/auth/login", payload, com.google.gson.JsonObject.class);

                JsonObject userNode = null;
                if (json.has("user") && json.get("user").isJsonObject()) {
                    userNode = json.getAsJsonObject("user");
                }

                String userId = null;
                String fullName = null;
                String email = null;
                String phone = null;
                String role = null;
                String authToken = null;

                if (userNode != null) {
                    userId = getAsString(userNode, "id");
                    fullName = getAsString(userNode, "fullName");
                    email = getAsString(userNode, "email");
                    phone = getAsString(userNode, "phoneNumber");
                    role = getAsString(userNode, "role");
                } else {
                    userId = getAsString(json, "userId");
                    fullName = getAsString(json, "fullName");
                    email = getAsString(json, "email");
                    phone = getAsString(json, "phoneNumber");
                    role = getAsString(json, "role");
                }

                authToken = getAsString(json, "authToken");

                if (json.has("authenticated") && json.get("authenticated").getAsBoolean()) {
                    // split fullName into first/last
                    String firstName = fullName != null ? fullName.split(" ")[0] : "";
                    String lastName = "";
                    if (fullName != null && fullName.contains(" ")) {
                        lastName = fullName.substring(fullName.indexOf(' ') + 1);
                    }

                    UserSession.getInstance().login(userId, firstName, lastName, email, phone, role, authToken);

                    Platform.runLater(() -> {
                        // Close login window and open main shell
                        Stage loginStage = (Stage) loginBtn.getScene().getWindow();
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/westoncodeops/sample/main.fxml"));
                            Parent mainRoot = loader.load();
                            Stage mainStage = new Stage();
                            mainStage.setTitle("Zonix Rental - Property Management System");
                            mainStage.setScene(new Scene(mainRoot, 1280, 700));
                            mainStage.setMinWidth(1200);
                            mainStage.setMinHeight(700);
                            mainStage.show();
                            loginStage.close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            statusLabel.setText("Failed to open main window: " + ex.getMessage());
                            loginBtn.setDisable(false);
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        statusLabel.setText("Invalid credentials");
                        loginBtn.setDisable(false);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setText("Login failed: " + e.getMessage());
                    loginBtn.setDisable(false);
                });
            }
        }).start();
    }

    private String getAsString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return null;
        return obj.get(key).getAsString();
    }

    private String getAsString(JsonObject parent, String key, String fallback) {
        String v = getAsString(parent, key);
        return v == null ? fallback : v;
    }

    private String getAsString(JsonElement node, String key) {
        if (node == null || !node.isJsonObject()) return null;
        return getAsString(node.getAsJsonObject(), key);
    }
}
