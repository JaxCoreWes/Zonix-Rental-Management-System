package com.westoncodeops.sample.controllers;

import com.google.gson.JsonObject;
import com.westoncodeops.sample.network.RestClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private Button registerBtn;
    @FXML private Label statusLabel;

    @FXML
    public void handleRegister() {
        String fn = firstNameField.getText().trim();
        String ln = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String pw = passwordField.getText();

        if (fn.isEmpty() || ln.isEmpty() || phone.isEmpty() || pw.isEmpty()) {
            statusLabel.setText("Please fill required fields");
            return;
        }

        registerBtn.setDisable(true);
        statusLabel.setText("Registering...");

        new Thread(() -> {
            try {
                Map<String, Object> payload = new HashMap<>();
                payload.put("firstName", fn);
                payload.put("lastName", ln);
                payload.put("phoneNumber", phone);
                payload.put("email", email.isEmpty() ? null : email);
                payload.put("password", pw);
                payload.put("role", "LANDLORD");

                JsonObject resp = RestClient.post("/users/register", payload, JsonObject.class);

                Platform.runLater(() -> {
                    firstNameField.clear();
                    lastNameField.clear();
                    emailField.clear();
                    phoneField.clear();
                    passwordField.clear();
                    statusLabel.setText("Registration successful. Redirecting to login...");

                    try {
                        Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/westoncodeops/sample/views/login.fxml"));
                        Stage stage = (Stage) registerBtn.getScene().getWindow();
                        stage.getScene().setRoot(loginRoot);
                        stage.setTitle("Sign In");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setText("Registration failed: " + e.getMessage());
                    registerBtn.setDisable(false);
                });
            }
        }).start();
    }
}
