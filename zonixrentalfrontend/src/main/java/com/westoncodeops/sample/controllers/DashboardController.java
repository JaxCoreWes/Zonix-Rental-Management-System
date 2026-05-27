package com.westoncodeops.sample.controllers;

import com.google.gson.JsonObject;
import com.westoncodeops.sample.network.RestClient;
import com.westoncodeops.sample.session.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * Controller for the Dashboard view with role-based metric display
 * Dynamically adjusts dashboard content based on logged-in user's role:
 * - LANDLORD: Financial metrics, revenue tracking, extension approvals
 * - CARETAKER: Operational metrics, unit occupancy, maintenance tickets
 */
public class DashboardController {

    @FXML
    private Label welcomeLabel;
    
    @FXML
    private Label totalTenantsLabel;
    
    @FXML
    private Label totalUnitsLabel;
    
    @FXML
    private Label availableUnitsLabel;
    
    @FXML
    private Label occupiedUnitsLabel;
    
    @FXML
    private Label pendingTicketsLabel;
    
    @FXML
    private Label pendingExtensionsLabel;
    
    @FXML
    private Label totalPaymentsLabel;
    
    @FXML
    private GridPane statsGrid;
    
    @FXML
    private VBox card1;
    
    @FXML
    private VBox card2;
    
    @FXML
    private VBox card3;
    
    @FXML
    private VBox card4;
    
    @FXML
    private VBox card5;
    
    @FXML
    private VBox card6;
    
    @FXML
    private VBox card7;

    private UserSession session;

    /**
     * Initialize the dashboard controller
     */
    @FXML
    public void initialize() {
        session = UserSession.getInstance();
        
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + session.getFullName());
        }

        loadDashboardData();
    }

    /**
     * Load dashboard data from backend API
     * Fetches different metrics based on user role
     */
    private void loadDashboardData() {
        new Thread(() -> {
            try {
                if (session.isLandlord()) {
                    loadLandlordDashboard();
                } else if (session.isCaretaker()) {
                    loadCaretakerDashboard();
                } else {
                    loadDefaultDashboard();
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.err.println("Error loading dashboard data: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }

    /**
     * Load LANDLORD-specific dashboard metrics
     * Focus: Financial health, revenue tracking, extension approvals
     */
    private void loadLandlordDashboard() throws Exception {
        String metricsResponse = RestClient.get("/dashboard/metrics?role=" + session.getRole(), String.class);
        JsonObject metrics = RestClient.getGson().fromJson(metricsResponse, JsonObject.class);

        BigDecimal totalRevenue = getBigDecimal(metrics, "totalRevenue");
        long pendingExtensions = getLong(metrics, "pendingExtensions");
        long totalUnits = getLong(metrics, "totalUnits");
        long occupiedUnits = getLong(metrics, "occupiedUnits");
        long availableUnits = getLong(metrics, "availableUnits");
        long totalTenants = getLong(metrics, "totalTenants");
        String systemAlerts = getString(metrics, "systemAlerts", "All financial streams stable.");

        Platform.runLater(() -> {
            updateCardLabel(card1, 0, "💰");
            updateCardLabel(card1, 1, "KES " + totalRevenue.setScale(2, RoundingMode.HALF_UP).toPlainString());
            updateCardLabel(card1, 2, "Total Revenue");

            updateCardLabel(card2, 0, "📅");
            updateCardLabel(card2, 1, String.valueOf(pendingExtensions));
            updateCardLabel(card2, 2, "Pending Extensions");

            updateCardLabel(card3, 0, "🏢");
            updateCardLabel(card3, 1, String.valueOf(totalUnits));
            updateCardLabel(card3, 2, "Total Units");

            updateCardLabel(card4, 0, "✅");
            updateCardLabel(card4, 1, String.valueOf(availableUnits));
            updateCardLabel(card4, 2, "Available Units");

            updateCardLabel(card5, 0, "🔒");
            updateCardLabel(card5, 1, String.valueOf(occupiedUnits));
            updateCardLabel(card5, 2, "Occupied Units");

            updateCardLabel(card6, 0, "👥");
            updateCardLabel(card6, 1, String.valueOf(totalTenants));
            updateCardLabel(card6, 2, "Total Tenants");

            updateCardLabel(card7, 0, "⚡");
            updateCardLabel(card7, 1, systemAlerts);
        });
    }

    /**
     * Load CARETAKER-specific dashboard metrics
     * Focus: Operational metrics, unit management, maintenance tracking
     */
    private void loadCaretakerDashboard() throws Exception {
        String metricsResponse = RestClient.get("/dashboard/metrics?role=" + session.getRole(), String.class);
        JsonObject metrics = RestClient.getGson().fromJson(metricsResponse, JsonObject.class);

        long totalTenants = getLong(metrics, "totalTenants");
        long totalUnits = getLong(metrics, "totalUnits");
        long availableUnits = getLong(metrics, "availableUnits");
        long occupiedUnits = getLong(metrics, "occupiedUnits");
        long pendingTickets = getLong(metrics, "pendingTickets");
        long resolvedTickets = getLong(metrics, "resolvedTickets");
        String maintenanceStatus = getString(metrics, "maintenanceStatus", "Operational dashboard live.");

        Platform.runLater(() -> {
            updateCardLabel(card1, 0, "👥");
            updateCardLabel(card1, 1, String.valueOf(totalTenants));
            updateCardLabel(card1, 2, "Total Tenants");

            updateCardLabel(card2, 0, "🏢");
            updateCardLabel(card2, 1, String.valueOf(totalUnits));
            updateCardLabel(card2, 2, "Total Units");

            updateCardLabel(card3, 0, "✅");
            updateCardLabel(card3, 1, String.valueOf(availableUnits));
            updateCardLabel(card3, 2, "Available Units");

            updateCardLabel(card4, 0, "🔒");
            updateCardLabel(card4, 1, String.valueOf(occupiedUnits));
            updateCardLabel(card4, 2, "Occupied Units");

            updateCardLabel(card5, 0, "🔧");
            updateCardLabel(card5, 1, String.valueOf(pendingTickets));
            updateCardLabel(card5, 2, "Pending Maintenance Tickets");

            updateCardLabel(card6, 0, "✔️");
            updateCardLabel(card6, 1, String.valueOf(resolvedTickets));
            updateCardLabel(card6, 2, "Resolved Tickets");

            updateCardLabel(card7, 0, "⚡");
            updateCardLabel(card7, 1, maintenanceStatus);
            updateCardLabel(card7, 2, "System Status");
        });
    }

    /**
     * Load default dashboard for other roles
     */
    private void loadDefaultDashboard() throws Exception {
        Platform.runLater(() -> {
            if (welcomeLabel != null) {
                welcomeLabel.setText("Welcome, " + session.getFullName());
            }

            updateCardLabel(card1, 0, "ℹ️");
            updateCardLabel(card1, 1, "N/A");
            updateCardLabel(card1, 2, "Not authenticated");

            updateCardLabel(card2, 0, "ℹ️");
            updateCardLabel(card2, 1, "N/A");
            updateCardLabel(card2, 2, "Login to view metrics");

            updateCardLabel(card3, 0, "ℹ️");
            updateCardLabel(card3, 1, "N/A");
            updateCardLabel(card3, 2, "Login to view metrics");

            updateCardLabel(card4, 0, "ℹ️");
            updateCardLabel(card4, 1, "N/A");
            updateCardLabel(card4, 2, "Login to view metrics");

            updateCardLabel(card5, 0, "ℹ️");
            updateCardLabel(card5, 1, "N/A");
            updateCardLabel(card5, 2, "Login to view metrics");

            updateCardLabel(card6, 0, "ℹ️");
            updateCardLabel(card6, 1, "N/A");
            updateCardLabel(card6, 2, "Login to view metrics");

            updateCardLabel(card7, 0, "ℹ️");
            updateCardLabel(card7, 1, "N/A");
            updateCardLabel(card7, 2, "Login to view metrics");
        });
    }

    /**
     * Helper method to update a label within a card VBox
     * @param card The VBox card container
     * @param index The index of the label to update (0=icon, 1=value, 2=description)
     * @param text The new text to set
     */
    private void updateCardLabel(VBox card, int index, String text) {
        if (card != null && card.getChildren().size() > index) {
            if (card.getChildren().get(index) instanceof Label) {
                Label label = (Label) card.getChildren().get(index);
                label.setText(text);
            }
        }
    }

    private BigDecimal getBigDecimal(JsonObject json, String key) {
        if (json == null || !json.has(key) || json.get(key).isJsonNull()) {
            return BigDecimal.ZERO;
        }
        try {
            return json.get(key).getAsBigDecimal();
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private long getLong(JsonObject json, String key) {
        if (json == null || !json.has(key) || json.get(key).isJsonNull()) {
            return 0L;
        }
        try {
            return json.get(key).getAsLong();
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private String getString(JsonObject json, String key, String fallback) {
        if (json == null || !json.has(key) || json.get(key).isJsonNull()) {
            return fallback;
        }
        return json.get(key).getAsString();
    }

    /**
     * Refresh dashboard data
     */
    @FXML
    public void refreshDashboard() {
        loadDashboardData();
    }
}

// Made with Bob