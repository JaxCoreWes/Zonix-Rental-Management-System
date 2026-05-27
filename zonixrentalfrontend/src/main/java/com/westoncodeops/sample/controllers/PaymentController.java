package com.westoncodeops.sample.controllers;

import com.google.gson.reflect.TypeToken;
import com.westoncodeops.sample.models.Payment;
import com.westoncodeops.sample.network.RestClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing payment records and M-Pesa transactions
 */
public class PaymentController {

    @FXML
    private TableView<Payment> paymentsTable;
    @FXML
    private TableColumn<Payment, Long> idColumn;
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Record Payment");
        alert.setHeaderText("Record New Payment");
        alert.setContentText("This feature will open a dialog to record a new payment transaction.");
        alert.showAndWait();
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
