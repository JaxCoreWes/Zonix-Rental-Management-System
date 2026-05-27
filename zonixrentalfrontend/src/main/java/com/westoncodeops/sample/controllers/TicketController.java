package com.westoncodeops.sample.controllers;

import com.google.gson.reflect.TypeToken;
import com.westoncodeops.sample.models.Ticket;
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
 * Controller for managing maintenance tickets view
 */
public class TicketController {

    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    private TableColumn<Ticket, Long> idColumn;
    @FXML
    private TableColumn<Ticket, String> titleColumn;
    @FXML
    private TableColumn<Ticket, String> categoryColumn;
    @FXML
    private TableColumn<Ticket, String> priorityColumn;
    @FXML
    private TableColumn<Ticket, String> statusColumn;
    @FXML
    private TableColumn<Ticket, String> unitNumberColumn;
    @FXML
    private TableColumn<Ticket, String> reportedByColumn;
    @FXML
    private TableColumn<Ticket, String> assignedToColumn;
    @FXML
    private TableColumn<Ticket, LocalDate> reportedDateColumn;
    @FXML
    private TableColumn<Ticket, Void> actionsColumn;

    @FXML
    private TextField searchField;
    @FXML
    private Label totalTicketsLabel;
    @FXML
    private Label pendingTicketsLabel;
    @FXML
    private Label resolvedTicketsLabel;

    private ObservableList<Ticket> ticketsList = FXCollections.observableArrayList();
    private ObservableList<Ticket> filteredList = FXCollections.observableArrayList();

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionsColumn();
        loadTickets();
    }

    /**
     * Setup table columns with property bindings
     */
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        unitNumberColumn.setCellValueFactory(new PropertyValueFactory<>("unitNumber"));
        reportedByColumn.setCellValueFactory(new PropertyValueFactory<>("reportedByName"));
        assignedToColumn.setCellValueFactory(new PropertyValueFactory<>("assignedToName"));
        reportedDateColumn.setCellValueFactory(new PropertyValueFactory<>("reportedDate"));

        // Custom cell factory for status column
        statusColumn.setCellFactory(column -> new TableCell<Ticket, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("RESOLVED".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; -fx-alignment: center;");
                    } else if ("PENDING".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; -fx-alignment: center;");
                    } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-alignment: center;");
                    }
                }
            }
        });

        // Custom cell factory for priority column
        priorityColumn.setCellFactory(column -> new TableCell<Ticket, String>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(priority);
                    if ("HIGH".equalsIgnoreCase(priority)) {
                        setStyle("-fx-text-fill: #DC2626; -fx-font-weight: bold;");
                    } else if ("MEDIUM".equalsIgnoreCase(priority)) {
                        setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #10B981;");
                    }
                }
            }
        });

        ticketsTable.setItems(filteredList);
    }

    /**
     * Setup actions column with buttons
     */
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<Ticket, Void>() {
            private final Button resolveButton = new Button("Resolve");

            {
                resolveButton.getStyleClass().add("button-success");
                resolveButton.setOnAction(event -> {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    handleResolveTicket(ticket);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    resolveButton.setDisable(ticket.isResolved());
                    setGraphic(resolveButton);
                }
            }
        });
    }

    /**
     * Load tickets from backend API
     * Endpoint: GET /api/v1/maintenance
     */
    @FXML
    public void loadTickets() {
        new Thread(() -> {
            try {
                // Fetch maintenance tickets from API
                String response = RestClient.get("/maintenance", String.class);
                List<Ticket> tickets = RestClient.getGson().fromJson(response,
                    new TypeToken<List<Ticket>>(){}.getType());

                Platform.runLater(() -> {
                    ticketsList.clear();
                    ticketsList.addAll(tickets);
                    filteredList.clear();
                    filteredList.addAll(tickets);
                    updateStats();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Failed to load tickets: " + e.getMessage());
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
            filteredList.setAll(ticketsList);
        } else {
            filteredList.clear();
            for (Ticket ticket : ticketsList) {
                if (ticket.getTitle().toLowerCase().contains(searchText) ||
                    ticket.getCategory().toLowerCase().contains(searchText) ||
                    (ticket.getUnitNumber() != null && ticket.getUnitNumber().toLowerCase().contains(searchText))) {
                    filteredList.add(ticket);
                }
            }
        }
        updateStats();
    }

    /**
     * Filter to show all tickets
     */
    @FXML
    public void filterAll() {
        filteredList.setAll(ticketsList);
        updateStats();
    }

    /**
     * Filter to show only pending tickets
     */
    @FXML
    public void filterPending() {
        filteredList.clear();
        for (Ticket ticket : ticketsList) {
            if (ticket.isPending()) {
                filteredList.add(ticket);
            }
        }
        updateStats();
    }

    /**
     * Filter to show only resolved tickets
     */
    @FXML
    public void filterResolved() {
        filteredList.clear();
        for (Ticket ticket : ticketsList) {
            if (ticket.isResolved()) {
                filteredList.add(ticket);
            }
        }
        updateStats();
    }

    /**
     * Update statistics labels
     */
    private void updateStats() {
        int total = filteredList.size();
        int pending = (int) filteredList.stream().filter(Ticket::isPending).count();
        int resolved = (int) filteredList.stream().filter(Ticket::isResolved).count();

        totalTicketsLabel.setText("Total Tickets: " + total);
        pendingTicketsLabel.setText("Pending: " + pending);
        resolvedTicketsLabel.setText("Resolved: " + resolved);
    }

    /**
     * Handle resolve ticket action
     * Calls PUT /api/v1/maintenance/{id}/resolve endpoint
     */
    private void handleResolveTicket(Ticket ticket) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Resolve Ticket");
        alert.setHeaderText("Resolve Ticket #" + ticket.getId());
        alert.setContentText("Are you sure you want to mark this ticket as resolved?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        // Call PUT endpoint to resolve ticket
                        RestClient.put("/maintenance/" + ticket.getId() + "/resolve", null, String.class);
                        
                        Platform.runLater(() -> {
                            showInfo("Ticket #" + ticket.getId() + " marked as resolved.");
                            loadTickets(); // Refresh the list
                        });
                        
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            showError("Failed to resolve ticket: " + e.getMessage());
                            e.printStackTrace();
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * Show create ticket dialog
     */
    @FXML
    public void showCreateTicketDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create Ticket");
        alert.setHeaderText("Create New Maintenance Ticket");
        alert.setContentText("This feature will open a dialog to create a new maintenance ticket.");
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
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

// Made with Bob
