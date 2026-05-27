package com.westoncodeops.sample.controllers;

import com.google.gson.reflect.TypeToken;
import com.westoncodeops.sample.models.Unit;
import com.westoncodeops.sample.network.RestClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for managing units view and tenant-to-unit assignments
 */
public class UnitController {

    @FXML
    private TableView<Unit> unitsTable;
    @FXML
    private TableColumn<Unit, Long> idColumn;
    @FXML
    private TableColumn<Unit, String> unitNumberColumn;
    @FXML
    private TableColumn<Unit, String> unitTypeColumn;
    @FXML
    private TableColumn<Unit, Double> rentAmountColumn;
    @FXML
    private TableColumn<Unit, String> statusColumn;
    @FXML
    private TableColumn<Unit, String> tenantNameColumn;
    @FXML
    private TableColumn<Unit, LocalDate> createdAtColumn;
    @FXML
    private TableColumn<Unit, Void> actionsColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalUnitsLabel;
    @FXML
    private Label availableUnitsLabel;
    @FXML
    private Label occupiedUnitsLabel;

    private ObservableList<Unit> unitsList = FXCollections.observableArrayList();
    private ObservableList<Unit> filteredList = FXCollections.observableArrayList();

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionsColumn();
        loadUnits();
    }

    /**
     * Setup table columns with property bindings
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        unitNumberColumn.setCellValueFactory(new PropertyValueFactory<>("unitNumber"));
        unitTypeColumn.setCellValueFactory(new PropertyValueFactory<>("unitType"));
        rentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("rentAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        tenantNameColumn.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Custom cell factory for status column with colored badges
        statusColumn.setCellFactory(column -> new TableCell<Unit, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("AVAILABLE".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-alignment: center;");
                    } else if ("OCCUPIED".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-alignment: center;");
                    }
                }
            }
        });

        unitsTable.setItems(filteredList);
    }

    /**
     * Setup actions column with buttons
     */
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<Unit, Void>() {
            private final Button toggleButton = new Button();

            {
                toggleButton.getStyleClass().add("button-secondary");
                toggleButton.setOnAction(event -> {
                    Unit unit = getTableView().getItems().get(getIndex());
                    handleToggleUnitStatus(unit);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Unit unit = getTableView().getItems().get(getIndex());
                    if (unit.isAvailable()) {
                        toggleButton.setText("Mark Occupied");
                        toggleButton.getStyleClass().removeAll("button-success", "button-secondary");
                        toggleButton.getStyleClass().add("button-secondary");
                    } else {
                        toggleButton.setText("Mark Available");
                        toggleButton.getStyleClass().removeAll("button-success", "button-secondary");
                        toggleButton.getStyleClass().add("button-success");
                    }
                    setGraphic(toggleButton);
                }
            }
        });
    }

    /**
     * Load units from backend API
     */
    @FXML
    public void loadUnits() {
        new Thread(() -> {
            try {
                // Fetch units from API endpoint
                String response = RestClient.get("/units", String.class);
                List<Unit> units = RestClient.getGson().fromJson(response,
                    new TypeToken<List<Unit>>(){}.getType());

                Platform.runLater(() -> {
                    unitsList.clear();
                    unitsList.addAll(units);
                    filteredList.clear();
                    filteredList.addAll(units);
                    updateStats();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to load units: " + e.getMessage());
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
            filteredList.setAll(unitsList);
        } else {
            filteredList.clear();
            for (Unit unit : unitsList) {
                if (unit.getUnitNumber().toLowerCase().contains(searchText) ||
                    unit.getUnitType().toLowerCase().contains(searchText) ||
                    (unit.getTenantName() != null && unit.getTenantName().toLowerCase().contains(searchText))) {
                    filteredList.add(unit);
                }
            }
        }
        updateStats();
    }

    /**
     * Filter to show all units
     */
    @FXML
    public void filterAll() {
        filteredList.setAll(unitsList);
        updateStats();
    }

    /**
     * Filter to show only available units
     */
    @FXML
    public void filterAvailable() {
        filteredList.clear();
        for (Unit unit : unitsList) {
            if (unit.isAvailable()) {
                filteredList.add(unit);
            }
        }
        updateStats();
    }

    /**
     * Filter to show only occupied units
     */
    @FXML
    public void filterOccupied() {
        filteredList.clear();
        for (Unit unit : unitsList) {
            if (unit.isOccupied()) {
                filteredList.add(unit);
            }
        }
        updateStats();
    }

    /**
     * Update statistics labels
     */
    private void updateStats() {
        int total = filteredList.size();
        int available = (int) filteredList.stream().filter(Unit::isAvailable).count();
        int occupied = (int) filteredList.stream().filter(Unit::isOccupied).count();

        totalUnitsLabel.setText("Total Units: " + total);
        availableUnitsLabel.setText("Available: " + available);
        occupiedUnitsLabel.setText("Occupied: " + occupied);
    }

    /**
     * Handle toggle unit status action (Available <-> Occupied)
     * Calls PUT /api/v1/units/{id}/status endpoint
     */
    private void handleToggleUnitStatus(Unit unit) {
        String newStatus = unit.isAvailable() ? "OCCUPIED" : "AVAILABLE";
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Change Unit Status");
        confirmAlert.setHeaderText("Change status for Unit " + unit.getUnitNumber());
        confirmAlert.setContentText("Change status from " + unit.getStatus() + " to " + newStatus + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                new Thread(() -> {
                    try {
                        // Create request body with new status
                        java.util.Map<String, String> requestBody = new java.util.HashMap<>();
                        requestBody.put("status", newStatus);
                        
                        // Call PUT endpoint
                        RestClient.put("/units/" + unit.getId() + "/status", requestBody, String.class);
                        
                        javafx.application.Platform.runLater(() -> {
                            showInfo("Unit " + unit.getUnitNumber() + " status updated to " + newStatus);
                            loadUnits(); // Refresh the list
                        });
                        
                    } catch (Exception e) {
                        javafx.application.Platform.runLater(() -> {
                            showError("Failed to update unit status: " + e.getMessage());
                            e.printStackTrace();
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * Show add unit dialog
     */
    @FXML
    public void showAddUnitDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Unit");
        alert.setHeaderText("Add New Unit");
        alert.setContentText("This feature will open a dialog to create a new unit.");
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

    /**
     * Show info alert
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

// Made with Bob
