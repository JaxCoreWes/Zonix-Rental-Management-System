package com.westoncodeops.sample.controllers;

import com.google.gson.reflect.TypeToken;
import com.westoncodeops.sample.models.Ticket;
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
import java.util.List;

public class TicketController {

    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    private TableColumn<Ticket, String> idColumn;
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

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionsColumn();
        loadTickets();
    }

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
                        setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B; -fx-alignment: center;");
                    } else if ("IN_PROGRESS".equalsIgnoreCase(status)) {
                        setStyle("-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; -fx-alignment: center;");
                    }
                }
            }
        });

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

    @FXML
    public void loadTickets() {
        new Thread(() -> {
            try {
                String response = RestClient.get("/tickets/allTickets", String.class);
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

    @FXML
    public void filterAll() {
        filteredList.setAll(ticketsList);
        updateStats();
    }

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

    private void updateStats() {
        int total = filteredList.size();
        int pending = (int) filteredList.stream().filter(Ticket::isPending).count();
        int resolved = (int) filteredList.stream().filter(Ticket::isResolved).count();

        totalTicketsLabel.setText("Total Tickets: " + total);
        pendingTicketsLabel.setText("Pending: " + pending);
        resolvedTicketsLabel.setText("Resolved: " + resolved);
    }

    private void handleResolveTicket(Ticket ticket) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Resolve Ticket");
        alert.setHeaderText("Resolve Ticket " + ticket.getTicketNumber());
        alert.setContentText("Are you sure you want to mark this ticket as resolved?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        RestClient.patch("/tickets/" + ticket.getId() + "/resolve", null, String.class);
                        
                        Platform.runLater(() -> {
                            showInfo("Ticket " + ticket.getTicketNumber() + " marked as resolved.");
                            loadTickets();
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

    @FXML
    public void showCreateTicketDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/westoncodeops/sample/views/add_ticket_modal.fxml"));
            Parent root = loader.load();

            AddTicketModalController modalController = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Create Maintenance Ticket");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            modalController.setModalStage(stage);
            modalController.setParentController(this);

            stage.showAndWait();

        } catch (IOException e) {
            showError("Failed to open Add Ticket modal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
