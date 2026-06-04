package com.westoncodeops.sample.controllers;

import com.westoncodeops.sample.network.RestClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AddTicketModalController {

    @FXML private TextField phoneField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextArea descriptionField;
    @FXML private Label statusLabel;
    @FXML private Button saveButton;

    private Stage modalStage;
    private TicketController parentController;

    public void setModalStage(Stage stage) { this.modalStage = stage; }
    public void setParentController(TicketController parent) { this.parentController = parent; }

    @FXML
    public void initialize() {
        categoryComboBox.setItems(javafx.collections.FXCollections.observableArrayList(
            "PLUMBING", "ELECTRICAL", "HVAC", "GENERAL"
        ));
    }

    @FXML private void saveTicket() {
        if (!validateInputs()) return;

        saveButton.setDisable(true);
        statusLabel.setText("Creating ticket...");
        statusLabel.setStyle("-fx-text-fill: #3B82F6;");

        new Thread(() -> {
            try {
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("phoneNumber", phoneField.getText().trim());
                requestData.put("category", categoryComboBox.getValue());
                requestData.put("description", descriptionField.getText().trim());

                RestClient.post("/tickets/requestTicket", requestData, String.class);

                Platform.runLater(() -> {
                    statusLabel.setText("✓ Ticket created successfully!");
                    statusLabel.setStyle("-fx-text-fill: #10B981;");
                    if (parentController != null) {
                        parentController.loadTickets();
                    }
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            Platform.runLater(() -> closeModal());
                        } catch (InterruptedException e) { e.printStackTrace(); }
                    }).start();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("✗ Error: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #EF4444;");
                    saveButton.setDisable(false);
                    e.printStackTrace();
                });
            }
        }).start();
    }

    @FXML private void closeModal() {
        if (modalStage != null) modalStage.close();
    }

    private boolean validateInputs() {
        if (phoneField.getText().trim().isEmpty()) {
            showValidationError("Tenant phone number is required");
            return false;
        }
        if (categoryComboBox.getValue() == null) {
            showValidationError("Please select a category");
            return false;
        }
        if (descriptionField.getText().trim().isEmpty()) {
            showValidationError("Description is required");
            return false;
        }
        return true;
    }

    private void showValidationError(String message) {
        statusLabel.setText("✗ " + message);
        statusLabel.setStyle("-fx-text-fill: #EF4444;");
    }
}
