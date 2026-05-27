package com.westoncodeops.sample.controllers;

import com.google.gson.reflect.TypeToken;
import com.westoncodeops.sample.models.Tenant;
import com.westoncodeops.sample.network.RestClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing tenants view and Add Tenant modal
 */
public class TenantController {

    // Tenants Table View Components
    @FXML
    private TableView<Tenant> tenantsTable;
    @FXML
    private TableColumn<Tenant, Long> idColumn;
    @FXML
    private TableColumn<Tenant, String> firstNameColumn;
    @FXML
    private TableColumn<Tenant, String> lastNameColumn;
    @FXML
    private TableColumn<Tenant, String> emailColumn;
    @FXML
    private TableColumn<Tenant, String> phoneColumn;
    @FXML
    private TableColumn<Tenant, String> nationalIdColumn;
    @FXML
    private TableColumn<Tenant, String> roleColumn;
    @FXML
    private TableColumn<Tenant, LocalDate> createdAtColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalTenantsLabel;
    @FXML
    private Label selectedTenantLabel;
    @FXML
    private Button addTenantBtn;

    // Add Tenant Modal Components
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField nationalIdField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label statusLabel;
    @FXML
    private Button saveButton;

    private ObservableList<Tenant> tenantsList = FXCollections.observableArrayList();
    private ObservableList<Tenant> filteredList = FXCollections.observableArrayList();
    private Stage modalStage;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Only initialize table if it exists (main view)
        if (tenantsTable != null) {
            setupTableColumns();
            setupTableSelectionListener();
            loadTenants();
        }

        // Only initialize form if it exists (modal view)
        if (roleComboBox != null) {
            setupRoleComboBox();
        }
    }

    /**
     * Setup table columns with property bindings
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        nationalIdColumn.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        tenantsTable.setItems(filteredList);
    }

    /**
     * Setup table selection listener
     */
    private void setupTableSelectionListener() {
        tenantsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTenantLabel.setText("Selected: " + newSelection.getFullName());
            } else {
                selectedTenantLabel.setText("Selected: None");
            }
        });
    }

    /**
     * Setup role combo box with available roles
     */
    private void setupRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList("TENANT", "CARETAKER", "ADMIN"));
        roleComboBox.setValue("TENANT");
    }

    /**
     * Load tenants from backend API
     */
    @FXML
    public void loadTenants() {
        new Thread(() -> {
            try {
                // Fetch tenants from API
                String response = RestClient.get("/users", String.class);
                List<Tenant> tenants = RestClient.getGson().fromJson(response, 
                    new TypeToken<List<Tenant>>(){}.getType());

                Platform.runLater(() -> {
                    tenantsList.clear();
                    tenantsList.addAll(tenants);
                    filteredList.clear();
                    filteredList.addAll(tenants);
                    updateStats();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to load tenants: " + e.getMessage());
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
            filteredList.setAll(tenantsList);
        } else {
            filteredList.clear();
            for (Tenant tenant : tenantsList) {
                if (tenant.getFirstName().toLowerCase().contains(searchText) ||
                    tenant.getLastName().toLowerCase().contains(searchText) ||
                    tenant.getEmail().toLowerCase().contains(searchText) ||
                    tenant.getPhoneNumber().contains(searchText)) {
                    filteredList.add(tenant);
                }
            }
        }
        updateStats();
    }

    /**
     * Update statistics labels
     */
    private void updateStats() {
        totalTenantsLabel.setText("Total Tenants: " + filteredList.size());
    }

    /**
     * Show Add Tenant modal dialog
     */
    @FXML
    public void showAddTenantModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/westoncodeops/sample/views/add_tenant_modal.fxml"));
            Parent root = loader.load();

            TenantController modalController = loader.getController();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Tenant");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            
            modalController.setModalStage(stage);
            modalController.setParentController(this);
            
            stage.showAndWait();

        } catch (IOException e) {
            showError("Failed to open Add Tenant modal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save new tenant
     */
    @FXML
    public void saveTenant() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Disable save button during processing
        saveButton.setDisable(true);
        statusLabel.setText("Saving tenant...");
        statusLabel.setStyle("-fx-text-fill: #3B82F6;");

        new Thread(() -> {
            try {
                // Prepare request data
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("firstName", firstNameField.getText().trim());
                requestData.put("lastName", lastNameField.getText().trim());
                requestData.put("email", emailField.getText().trim());
                requestData.put("phoneNumber", phoneField.getText().trim());
                requestData.put("nationalId", nationalIdField.getText().trim());
                requestData.put("password", passwordField.getText());
                requestData.put("role", roleComboBox.getValue());

                // Send POST request
                RestClient.post("/users/register", requestData, String.class);

                Platform.runLater(() -> {
                    statusLabel.setText("✓ Tenant saved successfully!");
                    statusLabel.setStyle("-fx-text-fill: #10B981;");
                    if (parentController != null) {
                        parentController.loadTenants();
                    }
                    
                    // Close modal after short delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            Platform.runLater(this::closeModal);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
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

    /**
     * Validate form inputs
     */
    private boolean validateInputs() {
        if (firstNameField.getText().trim().isEmpty()) {
            showValidationError("First name is required");
            return false;
        }
        if (lastNameField.getText().trim().isEmpty()) {
            showValidationError("Last name is required");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showValidationError("Email is required");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showValidationError("Phone number is required");
            return false;
        }
        if (nationalIdField.getText().trim().isEmpty()) {
            showValidationError("National ID is required");
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            showValidationError("Password is required");
            return false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showValidationError("Passwords do not match");
            return false;
        }
        if (roleComboBox.getValue() == null) {
            showValidationError("Please select a role");
            return false;
        }
        return true;
    }

    /**
     * Show validation error message
     */
    private void showValidationError(String message) {
        statusLabel.setText("✗ " + message);
        statusLabel.setStyle("-fx-text-fill: #EF4444;");
    }

    /**
     * Close the modal
     */
    @FXML
    public void closeModal() {
        if (modalStage != null) {
            modalStage.close();
        }
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
     * Set the modal stage reference
     */
    public void setModalStage(Stage stage) {
        this.modalStage = stage;
    }

    /**
     * Set parent controller reference to refresh data after save
     */
    private TenantController parentController;
    
    public void setParentController(TenantController parent) {
        this.parentController = parent;
    }
}

// Made with Bob
