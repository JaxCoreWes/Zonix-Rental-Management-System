package com.westoncodeops.sample.controllers;

import com.google.gson.reflect.TypeToken;
import com.westoncodeops.sample.models.Payment;
import com.westoncodeops.sample.network.RestClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing payment records and M-Pesa transactions
 */
public class PaymentController {

    @FXML
    private TableView<Payment> paymentsTable;
    @FXML
    private TableColumn<Payment, String> idColumn;
    @FXML
    private TableColumn<Payment, String> tenantNameColumn;
    @FXML
    private TableColumn<Payment, String> unitNumberColumn;
    @FXML
    private TableColumn<Payment, Double> amountColumn;
    @FXML
    private TableColumn<Payment, String> paymentMethodColumn;
    @FXML
    private TableColumn<Payment, String> mpesaReceiptColumn;
    @FXML
    private TableColumn<Payment, String> phoneNumberColumn;
    @FXML
    private TableColumn<Payment, String> statusColumn;
    @FXML
    private TableColumn<Payment, LocalDateTime> paymentDateColumn;
    @FXML
    private TableColumn<Payment, String> paymentForColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalPaymentsLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Label completedPaymentsLabel;
    @FXML
    private Label pendingPaymentsLabel;

    private ObservableList<Payment> paymentsList = FXCollections.observableArrayList();
    private ObservableList<Payment> filteredList = FXCollections.observableArrayList();

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        loadPayments();
    }

    /**
     * Setup table columns with property bindings
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        tenantNameColumn.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
        unitNumberColumn.setCellValueFactory(new PropertyValueFactory<>("unitNumber"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        mpesaReceiptColumn.setCellValueFactory(new PropertyValueFactory<>("mpesaReceiptNumber"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        paymentForColumn.setCellValueFactory(new PropertyValueFactory<>("paymentFor"));

        // Custom cell factory for amount column with currency formatting
        amountColumn.setCellFactory(column -> new TableCell<Payment, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("KES %.2f", amount));
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        // Custom cell factory for status column
        statusColumn.setCellFactory(column -> new TableCell<Payment, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("COMPLETED".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-alignment: center;");
                    } else if ("PENDING".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-alignment: center;");
                    } else if ("PAID".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-alignment: center;");
                    } else if ("FAILED".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-alignment: center;");
                    }
                }
            }
        });

        // Custom cell factory for payment method column
        paymentMethodColumn.setCellFactory(column -> new TableCell<Payment, String>() {
            @Override
            protected void updateItem(String method, boolean empty) {
                super.updateItem(method, empty);
                if (empty || method == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(method);
                    if ("MPESA".equalsIgnoreCase(method)) {
                        setStyle("-fx-text-fill: #10B981; -fx-font-weight: bold;");
                    }
                }
            }
        });

        paymentsTable.setItems(filteredList);
    }

    /**
     * Load payments from backend API
     */
    @FXML
    public void loadPayments() {
        new Thread(() -> {
            try {
                String response = RestClient.get("/payments", String.class);
                List<Payment> payments = RestClient.getGson().fromJson(response, 
                    new TypeToken<List<Payment>>(){}.getType());

                Platform.runLater(() -> {
                    paymentsList.clear();
                    paymentsList.addAll(payments);
                    filteredList.clear();
                    filteredList.addAll(payments);
                    updateStats();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to load payments: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    /**
     * Handle search functionality
     */
    @FXML
    public void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            filteredList.setAll(paymentsList);
        } else {
            filteredList.clear();
            for (Payment payment : paymentsList) {
                if (payment.getTenantName().toLowerCase().contains(searchText) ||
                    payment.getUnitNumber().toLowerCase().contains(searchText) ||
                    (payment.getMpesaReceiptNumber() != null && 
                     payment.getMpesaReceiptNumber().toLowerCase().contains(searchText)) ||
                    (payment.getPhoneNumber() != null && 
                     payment.getPhoneNumber().contains(searchText))) {
                    filteredList.add(payment);
                }
            }
        }
        updateStats();
    }

    /**
     * Filter to show all payments
     */
    @FXML
    public void filterAll() {
        filteredList.setAll(paymentsList);
        updateStats();
    }

    /**
     * Filter to show only completed payments
     */
    @FXML
    public void filterCompleted() {
        filteredList.clear();
        for (Payment payment : paymentsList) {
            if (payment.isCompleted()) {
                filteredList.add(payment);
            }
        }
        updateStats();
    }

    /**
     * Filter to show only pending payments
     */
    @FXML
    public void filterPending() {
        filteredList.clear();
        for (Payment payment : paymentsList) {
            if (payment.isPending()) {
                filteredList.add(payment);
            }
        }
        updateStats();
    }

    /**
     * Filter to show only failed payments
     */
    @FXML
    public void filterFailed() {
        filteredList.clear();
        for (Payment payment : paymentsList) {
            if (payment.isFailed()) {
                filteredList.add(payment);
            }
        }
        updateStats();
    }

    /**
     * Update statistics labels
     */
    private void updateStats() {
        int total = filteredList.size();
        int completed = (int) filteredList.stream().filter(Payment::isCompleted).count();
        int pending = (int) filteredList.stream().filter(Payment::isPending).count();
        
        double totalAmount = filteredList.stream()
            .filter(Payment::isCompleted)
            .mapToDouble(Payment::getAmount)
            .sum();

        totalPaymentsLabel.setText("Total Payments: " + total);
        totalAmountLabel.setText(String.format("Total Amount: KES %.2f", totalAmount));
        completedPaymentsLabel.setText("Completed: " + completed);
        pendingPaymentsLabel.setText("Pending: " + pending);
    }

    /**
     * Show record payment dialog
     */
    @FXML
    public void showRecordPaymentDialog() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Pay Rent via M-Pesa");
        dialog.setHeaderText("Enter your phone number and amount to receive an STK Push.");
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType("Pay", ButtonData.OK_DONE), ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField phoneField = new TextField();
        phoneField.setPromptText("2547XXXXXXXX");

        TextField amountField = new TextField();
        amountField.setPromptText("Amount in KES");

        TextField referenceField = new TextField();
        referenceField.setPromptText("Account reference (e.g. RENT)");
        referenceField.setText("RENT");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Description (optional)");
        descriptionField.setText("Monthly rent payment");

        grid.add(new Label("Phone Number:"), 0, 0);
        grid.add(phoneField, 1, 0);
        grid.add(new Label("Amount (KES):"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Account Reference:"), 0, 2);
        grid.add(referenceField, 1, 2);
        grid.add(new Label("Description:"), 0, 3);
        grid.add(descriptionField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Node payButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        payButton.setDisable(true);

        Runnable inputValidator = () -> payButton.setDisable(
            phoneField.getText().trim().isEmpty() ||
            amountField.getText().trim().isEmpty() ||
            !amountField.getText().trim().matches("^\\d+(\\.\\d{1,2})?$")
        );

        phoneField.textProperty().addListener((observable, oldValue, newValue) -> inputValidator.run());
        amountField.textProperty().addListener((observable, oldValue, newValue) -> inputValidator.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                Map<String, String> result = new HashMap<>();
                result.put("phoneNumber", phoneField.getText().trim());
                result.put("amount", amountField.getText().trim());
                result.put("accountReference", referenceField.getText().trim());
                result.put("transactionDesc", descriptionField.getText().trim());
                return result;
            }
            return null;
        });

        Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(input -> {
            new Thread(() -> {
                try {
                    Map<String, Object> requestBody = new HashMap<>();
                    requestBody.put("phoneNumber", input.get("phoneNumber"));
                    requestBody.put("amount", Double.parseDouble(input.get("amount")));
                    requestBody.put("accountReference", input.get("accountReference"));
                    requestBody.put("transactionDesc", input.get("transactionDesc"));

                    String response = RestClient.post("/daraja/stkpush", requestBody, String.class);
                    Map<?, ?> responseMap = RestClient.getGson().fromJson(response, Map.class);

                    Platform.runLater(() -> {
                        if (Boolean.TRUE.equals(responseMap.get("success"))) {
                            showInfo("STK Push sent successfully. Check your phone for the M-Pesa prompt.");
                        } else {
                            showError("Failed to send STK Push: " + responseMap.get("message"));
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showError("Failed to initiate M-Pesa payment: " + e.getMessage());
                    });
                }
            }).start();
        });
    }

    /**
     * Show error alert
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

// Made with Bob
