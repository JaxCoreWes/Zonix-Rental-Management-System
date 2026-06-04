package com.westoncodeops.sample.controllers;

import com.google.gson.reflect.TypeToken;
import com.westoncodeops.sample.models.Unit;
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
                        toggleButton.setText("Assign Tenant");
                        toggleButton.getStyleClass().removeAll("button-success", "button-secondary");
                        toggleButton.getStyleClass().add("button-secondary");
                    } else {
                        toggleButton.setText("Unassign Tenant");
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
        if (unit.isAvailable()) {
            String tenantPhone = promptForTenantPhone(unit);
            if (tenantPhone == null || tenantPhone.isBlank()) {
                return;
            }

            new Thread(() -> {
                try {
                    Map<String, String> requestBody = new HashMap<>();
                    requestBody.put("tenantPhoneNumber", tenantPhone);

                    RestClient.put("/units/" + unit.getId() + "/assign-tenant", requestBody, String.class);

                    Platform.runLater(() -> {
                        showInfo("Tenant assigned to unit " + unit.getUnitNumber());
                        loadUnits();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showError("Failed to assign tenant: " + e.getMessage());
                        e.printStackTrace();
                    });
                }
            }).start();
        } else {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Unassign Tenant");
            confirmAlert.setHeaderText("Remove tenant from Unit " + unit.getUnitNumber());
            confirmAlert.setContentText("This will mark the unit available again and remove the assigned tenant.");

            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    new Thread(() -> {
                        try {
                            Map<String, String> requestBody = new HashMap<>();
                            requestBody.put("status", "AVAILABLE");

                            RestClient.put("/units/" + unit.getId() + "/status", requestBody, String.class);

                            Platform.runLater(() -> {
                                showInfo("Unit " + unit.getUnitNumber() + " is now available");
                                loadUnits();
                            });
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                showError("Failed to unassign tenant: " + e.getMessage());
                                e.printStackTrace();
                            });
                        }
                    }).start();
                }
            });
        }
    }

    private String promptForTenantPhone(Unit unit) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Assign Tenant");
        dialog.setHeaderText("Assign a registered tenant to unit " + unit.getUnitNumber());
        dialog.setContentText("Enter tenant phone number:");

        return dialog.showAndWait().orElse(null);
    }

    /**
     * Show add unit dialog
     */
    @FXML
    public void showAddUnitDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/westoncodeops/sample/views/add_unit_modal.fxml"));
            Parent root = loader.load();

            AddUnitModalController modalController = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Unit");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            modalController.setModalStage(stage);
            modalController.setParentController(this);

            stage.showAndWait();

        } catch (IOException e) {
            showError("Failed to open Add Unit modal: " + e.getMessage());
            e.printStackTrace();
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
