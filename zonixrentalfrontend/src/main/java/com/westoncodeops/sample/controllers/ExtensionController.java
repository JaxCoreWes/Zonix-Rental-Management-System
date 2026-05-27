package com.westoncodeops.sample.controllers;

import com.google.gson.reflect.TypeToken;
import com.westoncodeops.sample.models.Extension;
import com.westoncodeops.sample.network.RestClient;
import com.westoncodeops.sample.session.UserSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing payment extension requests
 * LANDLORD-ONLY: Only users with LANDLORD role can approve/reject extensions
 */
public class ExtensionController {
    
    private UserSession session;

    @FXML
    private TableView<Extension> extensionsTable;
    @FXML
    private TableColumn<Extension, Long> idColumn;
    @FXML
    private TableColumn<Extension, String> tenantNameColumn;
    @FXML
    private TableColumn<Extension, String> unitNumberColumn;
    @FXML
    private TableColumn<Extension, Double> amountDueColumn;
    @FXML
    private TableColumn<Extension, LocalDate> requestedDateColumn;
    @FXML
    private TableColumn<Extension, LocalDate> promisedDateColumn;
    @FXML
    private TableColumn<Extension, String> reasonColumn;
    @FXML
    private TableColumn<Extension, String> statusColumn;
    @FXML
    private TableColumn<Extension, Void> actionsColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalExtensionsLabel;
    @FXML
    private Label pendingExtensionsLabel;
    @FXML
    private Label approvedExtensionsLabel;
    @FXML
    private Label rejectedExtensionsLabel;

    private ObservableList<Extension> extensionsList = FXCollections.observableArrayList();
    private ObservableList<Extension> filteredList = FXCollections.observableArrayList();

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        session = UserSession.getInstance();
        setupTableColumns();
        setupActionsColumn();
        loadExtensions();
    }

    /**
     * Setup table columns with property bindings
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        tenantNameColumn.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
        unitNumberColumn.setCellValueFactory(new PropertyValueFactory<>("unitNumber"));
        amountDueColumn.setCellValueFactory(new PropertyValueFactory<>("amountDue"));
        requestedDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestedDate"));
        promisedDateColumn.setCellValueFactory(new PropertyValueFactory<>("promisedPaymentDate"));
        reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Custom cell factory for status column
        statusColumn.setCellFactory(column -> new TableCell<Extension, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("APPROVED".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-alignment: center;");
                    } else if ("PENDING".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-alignment: center;");
                    } else if ("REJECTED".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-alignment: center;");
                    }
                }
            }
        });

        extensionsTable.setItems(filteredList);
    }

    /**
     * Setup actions column with approve/reject buttons
     * Only LANDLORD can approve/reject extensions
     */
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<Extension, Void>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            private final HBox buttonBox = new HBox(5, approveButton, rejectButton);

            {
                approveButton.getStyleClass().add("button-success");
                rejectButton.getStyleClass().add("button-danger");
                
                approveButton.setOnAction(event -> {
                    Extension extension = getTableView().getItems().get(getIndex());
                    handleApproveExtension(extension);
                });
                
                rejectButton.setOnAction(event -> {
                    Extension extension = getTableView().getItems().get(getIndex());
                    handleRejectExtension(extension);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Extension extension = getTableView().getItems().get(getIndex());
                    boolean isPending = extension.isPending();
                    boolean canApprove = session.canApproveExtensions();
                    
                    // Only enable buttons if user is LANDLORD and extension is pending
                    approveButton.setDisable(!isPending || !canApprove);
                    rejectButton.setDisable(!isPending || !canApprove);
                    setGraphic(buttonBox);
                }
            }
        });
    }

    /**
     * Load extensions from backend API
     */
    @FXML
    public void loadExtensions() {
        new Thread(() -> {
            try {
                String response = RestClient.get("/extensions", String.class);
                List<Extension> extensions = RestClient.getGson().fromJson(response, 
                    new TypeToken<List<Extension>>(){}.getType());

                Platform.runLater(() -> {
                    extensionsList.clear();
                    extensionsList.addAll(extensions);
                    filteredList.clear();
                    filteredList.addAll(extensions);
                    updateStats();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to load extensions: " + e.getMessage());
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
            filteredList.setAll(extensionsList);
        } else {
            filteredList.clear();
            for (Extension extension : extensionsList) {
                if (extension.getTenantName().toLowerCase().contains(searchText) ||
                    extension.getUnitNumber().toLowerCase().contains(searchText)) {
                    filteredList.add(extension);
                }
            }
        }
        updateStats();
    }

    /**
     * Filter to show all extensions
     */
    @FXML
    public void filterAll() {
        filteredList.setAll(extensionsList);
        updateStats();
    }

    /**
     * Filter to show only pending extensions
     */
    @FXML
    public void filterPending() {
        filteredList.clear();
        for (Extension extension : extensionsList) {
            if (extension.isPending()) {
                filteredList.add(extension);
            }
        }
        updateStats();
    }

    /**
     * Filter to show only approved extensions
     */
    @FXML
    public void filterApproved() {
        filteredList.clear();
        for (Extension extension : extensionsList) {
            if (extension.isApproved()) {
                filteredList.add(extension);
            }
        }
        updateStats();
    }

    /**
     * Filter to show only rejected extensions
     */
    @FXML
    public void filterRejected() {
        filteredList.clear();
        for (Extension extension : extensionsList) {
            if (extension.isRejected()) {
                filteredList.add(extension);
            }
        }
        updateStats();
    }

    /**
     * Update statistics labels
     */
    private void updateStats() {
        int total = filteredList.size();
        int pending = (int) filteredList.stream().filter(Extension::isPending).count();
        int approved = (int) filteredList.stream().filter(Extension::isApproved).count();
        int rejected = (int) filteredList.stream().filter(Extension::isRejected).count();

        totalExtensionsLabel.setText("Total Requests: " + total);
        pendingExtensionsLabel.setText("Pending: " + pending);
        approvedExtensionsLabel.setText("Approved: " + approved);
        rejectedExtensionsLabel.setText("Rejected: " + rejected);
    }

    /**
     * Handle approve extension action
     * Calls POST /api/v1/extensions/{id}/action endpoint with action=APPROVE
     * LANDLORD-ONLY operation
     */
    private void handleApproveExtension(Extension extension) {
        if (!session.canApproveExtensions()) {
            showError("Access Denied: Only LANDLORD can approve extensions.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Approve Extension");
        alert.setHeaderText("Approve Extension Request #" + extension.getId());
        alert.setContentText("Approve extension for " + extension.getTenantName() +
                           " until " + extension.getPromisedPaymentDate() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        // Prepare request body
                        Map<String, String> requestBody = new HashMap<>();
                        requestBody.put("action", "APPROVE");
                        
                        // Call POST endpoint to approve extension
                        RestClient.post("/extensions/" + extension.getId() + "/action",
                                      requestBody, String.class);
                        
                        Platform.runLater(() -> {
                            showInfo("Extension request #" + extension.getId() + " approved successfully.");
                            loadExtensions(); // Refresh the list
                        });
                        
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            showError("Failed to approve extension: " + e.getMessage());
                            e.printStackTrace();
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * Handle reject extension action
     * Calls POST /api/v1/extensions/{id}/action endpoint with action=REJECT
     * LANDLORD-ONLY operation
     */
    private void handleRejectExtension(Extension extension) {
        if (!session.canApproveExtensions()) {
            showError("Access Denied: Only LANDLORD can reject extensions.");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject Extension");
        dialog.setHeaderText("Reject Extension Request #" + extension.getId());
        dialog.setContentText("Please enter rejection reason:");
        
        dialog.showAndWait().ifPresent(reason -> {
            if (!reason.trim().isEmpty()) {
                new Thread(() -> {
                    try {
                        // Prepare request body
                        Map<String, String> requestBody = new HashMap<>();
                        requestBody.put("action", "REJECT");
                        requestBody.put("reason", reason.trim());
                        
                        // Call POST endpoint to reject extension
                        RestClient.post("/extensions/" + extension.getId() + "/action",
                                      requestBody, String.class);
                        
                        Platform.runLater(() -> {
                            showInfo("Extension request #" + extension.getId() + " rejected.");
                            loadExtensions(); // Refresh the list
                        });
                        
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            showError("Failed to reject extension: " + e.getMessage());
                            e.printStackTrace();
                        });
                    }
                }).start();
            }
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

    /**
     * Show info alert
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

// Made with Bob
