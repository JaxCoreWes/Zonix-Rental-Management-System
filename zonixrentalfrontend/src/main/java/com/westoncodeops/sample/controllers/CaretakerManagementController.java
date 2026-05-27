package com.westoncodeops.sample.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.westoncodeops.sample.network.RestClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;
import java.util.Map;

public class CaretakerManagementController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField phoneField;
    @FXML private TextField passwordField;
    @FXML private Button createBtn;
    @FXML private Label statusLabel;

    @FXML private TableView<JsonObject> table;
    @FXML private TableColumn<JsonObject, String> colName;
    @FXML private TableColumn<JsonObject, String> colPhone;

    private final ObservableList<JsonObject> items = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().get("fullName").getAsString()));
        colPhone.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().get("phoneNumber").getAsString()));
        table.setItems(items);
        loadCaretakers();
    }

    private void loadCaretakers() {
        statusLabel.setText("Loading...");
        new Thread(() -> {
            try {
                JsonArray arr = RestClient.get("/users/caretakers", JsonArray.class);
                Platform.runLater(() -> {
                    items.clear();
                    for (JsonElement e : arr) {
                        items.add(e.getAsJsonObject());
                    }
                    statusLabel.setText("");
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> statusLabel.setText("Failed to load caretakers: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void handleCreate() {
        String fn = firstNameField.getText().trim();
        String ln = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String pw = passwordField.getText();

        if (fn.isEmpty() || ln.isEmpty() || phone.isEmpty() || pw.isEmpty()) {
            statusLabel.setText("Please fill required fields");
            return;
        }

        createBtn.setDisable(true);
        statusLabel.setText("Creating...");

        new Thread(() -> {
            try {
                Map<String, Object> payload = new HashMap<>();
                payload.put("firstName", fn);
                payload.put("lastName", ln);
                payload.put("phoneNumber", phone);
                payload.put("email", null);
                payload.put("password", pw);
                payload.put("role", "CARETAKER");

                RestClient.post("/users/register", payload, String.class);

                Platform.runLater(() -> {
                    firstNameField.clear();
                    lastNameField.clear();
                    phoneField.clear();
                    passwordField.clear();
                    statusLabel.setText("Caretaker created");
                    createBtn.setDisable(false);
                    loadCaretakers();
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setText("Failed to create: " + e.getMessage());
                    createBtn.setDisable(false);
                });
            }
        }).start();
    }
}
