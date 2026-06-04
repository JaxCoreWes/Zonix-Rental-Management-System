package com.westoncodeops.sample.controllers;

import com.westoncodeops.sample.network.RestClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AddUnitModalController {

    @FXML private TextField unitNumberField;
    @FXML private TextField floorField;
    @FXML private TextField rentAmountField;
    @FXML private Label statusLabel;
    @FXML private Button saveButton;

    private Stage modalStage;
    private UnitController parentController;

    public void setModalStage(Stage stage) { this.modalStage = stage; }
    public void setParentController(UnitController parent) { this.parentController = parent; }

    @FXML private void saveUnit() {
        if (!validateInputs()) return;

        saveButton.setDisable(true);
        statusLabel.setText("Saving unit...");
        statusLabel.setStyle("-fx-text-fill: #3B82F6;");

        new Thread(() -> {
            try {
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("unitNumber", unitNumberField.getText().trim());
                requestData.put("floor", Integer.parseInt(floorField.getText().trim()));
                requestData.put("rentAmount", Double.parseDouble(rentAmountField.getText().trim()));

                RestClient.post("/units", requestData, String.class);

                Platform.runLater(() -> {
                    statusLabel.setText("✓ Unit saved successfully!");
                    statusLabel.setStyle("-fx-text-fill: #10B981;");
                    if (parentController != null) {
                        parentController.loadUnits();
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
        if (unitNumberField.getText().trim().isEmpty()) {
            showValidationError("Unit number is required");
            return false;
        }
        if (floorField.getText().trim().isEmpty()) {
            showValidationError("Floor is required");
            return false;
        }
        try {
            Integer.parseInt(floorField.getText().trim());
        } catch (NumberFormatException e) {
            showValidationError("Floor must be a valid number");
            return false;
        }
        if (rentAmountField.getText().trim().isEmpty()) {
            showValidationError("Rent amount is required");
            return false;
        }
        try {
            Double.parseDouble(rentAmountField.getText().trim());
        } catch (NumberFormatException e) {
            showValidationError("Rent amount must be a valid number");
            return false;
        }
        return true;
    }

    private void showValidationError(String message) {
        statusLabel.setText("✗ " + message);
        statusLabel.setStyle("-fx-text-fill: #EF4444;");
    }
}
