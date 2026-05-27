package com.westoncodeops.sample.controllers;

import com.westoncodeops.sample.session.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Main controller managing the application shell, navigation state transitions,
 * center view swapping, and role-based access control
 */
public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label headerTitle;

    @FXML
    private Label headerSubtitle;

    @FXML
    private Label userPillLabel;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button tenantsBtn;

    @FXML
    private Button unitsBtn;

    @FXML
    private Button ticketsBtn;

    @FXML
    private Button extensionsBtn;

    @FXML
    private Button paymentsBtn;

    @FXML
    private Button caretakersBtn;

    private Button currentActiveButton;
    private UserSession session;

    /**
     * Refresh the currently active view when the header refresh button is clicked
     */
    @FXML
    private void refreshCurrentView() {
        if (currentActiveButton != null) {
            currentActiveButton.fire();
        } else {
            showDashboard();
        }
    }

    /**
     * Initialize the controller - called automatically after FXML loading
     */
    @FXML
    public void initialize() {
        session = UserSession.getInstance();
        
        if (userPillLabel != null) {
            userPillLabel.setText(session.getFullName());
        }

        // Apply role-based navigation restrictions
        applyRoleBasedRestrictions();
        
        // Load dashboard view by default
        showDashboard();
    }

    /**
     * Apply role-based restrictions to navigation buttons
     * LANDLORD: Can access Dashboard, Extensions, Payments
     * CARETAKER: Can access Dashboard, Tenants, Units, Tickets
     */
    private void applyRoleBasedRestrictions() {
        if (session.isLandlord()) {
            // Landlord sees all navigation options
            if (dashboardBtn != null) { dashboardBtn.setVisible(true); dashboardBtn.setManaged(true); }
            if (tenantsBtn != null) { tenantsBtn.setVisible(true); tenantsBtn.setManaged(true); }
            if (unitsBtn != null) { unitsBtn.setVisible(true); unitsBtn.setManaged(true); }
            if (ticketsBtn != null) { ticketsBtn.setVisible(true); ticketsBtn.setManaged(true); }
            if (caretakersBtn != null) { caretakersBtn.setVisible(true); caretakersBtn.setManaged(true); }
            if (extensionsBtn != null) { extensionsBtn.setVisible(true); extensionsBtn.setManaged(true); }
            if (paymentsBtn != null) { paymentsBtn.setVisible(true); paymentsBtn.setManaged(true); }
        } else if (session.isCaretaker()) {
            // Caretaker can access: Dashboard, Tenants, Units, Maintenance
            if (dashboardBtn != null) { dashboardBtn.setVisible(true); dashboardBtn.setManaged(true); }
            if (tenantsBtn != null) { tenantsBtn.setVisible(true); tenantsBtn.setManaged(true); }
            if (unitsBtn != null) { unitsBtn.setVisible(true); unitsBtn.setManaged(true); }
            if (ticketsBtn != null) { ticketsBtn.setVisible(true); ticketsBtn.setManaged(true); }
            if (caretakersBtn != null) { caretakersBtn.setVisible(false); caretakersBtn.setManaged(false); }
            if (extensionsBtn != null) { extensionsBtn.setVisible(false); extensionsBtn.setManaged(false); }
            if (paymentsBtn != null) { paymentsBtn.setVisible(false); paymentsBtn.setManaged(false); }
        } else {
            // Tenant or other roles should only see dashboard by default
            if (dashboardBtn != null) { dashboardBtn.setVisible(true); dashboardBtn.setManaged(true); }
            if (tenantsBtn != null) { tenantsBtn.setVisible(false); tenantsBtn.setManaged(false); }
            if (unitsBtn != null) { unitsBtn.setVisible(false); unitsBtn.setManaged(false); }
            if (ticketsBtn != null) { ticketsBtn.setVisible(false); ticketsBtn.setManaged(false); }
            if (caretakersBtn != null) { caretakersBtn.setVisible(false); caretakersBtn.setManaged(false); }
            if (extensionsBtn != null) { extensionsBtn.setVisible(false); extensionsBtn.setManaged(false); }
            if (paymentsBtn != null) { paymentsBtn.setVisible(false); paymentsBtn.setManaged(false); }
        }
    }

    /**
     * Show Dashboard view
     */
    @FXML
    private void showDashboard() {
        loadView("views/dashboard.fxml", "Dashboard", "Overview of your property management system");
        setActiveButton(dashboardBtn);
    }

    /**
     * Show Tenants view (CARETAKER and ADMIN only)
     */
    @FXML
    private void showTenants() {
        if (!session.canManageTenants()) {
            showAccessDenied("Tenants Management");
            return;
        }
        loadView("views/tenants.fxml", "Tenants", "Manage tenant information and registrations");
        setActiveButton(tenantsBtn);
    }

    /**
     * Show Units view (CARETAKER and ADMIN only)
     */
    @FXML
    private void showUnits() {
        if (!session.canManageUnits()) {
            showAccessDenied("Units Management");
            return;
        }
        loadView("views/units.fxml", "Units", "Track apartment availability and assignments");
        setActiveButton(unitsBtn);
    }

    /**
     * Show Maintenance Tickets view (CARETAKER and ADMIN only)
     */
    @FXML
    private void showTickets() {
        if (!session.canManageTickets()) {
            showAccessDenied("Maintenance Tickets");
            return;
        }
        loadView("views/tickets.fxml", "Maintenance Tickets", "Manage caretaker tickets and resolutions");
        setActiveButton(ticketsBtn);
    }

    /**
     * Show Extensions view (LANDLORD and ADMIN only)
     */
    @FXML
    private void showExtensions() {
        if (!session.canAccessExtensions()) {
            showAccessDenied("Payment Extensions");
            return;
        }
        loadView("views/extensions.fxml", "Payment Extensions", "Review and approve promise-to-pay requests");
        setActiveButton(extensionsBtn);
    }

    /**
     * Show Payments view (LANDLORD and ADMIN only)
     */
    @FXML
    private void showPayments() {
        if (!session.canViewPayments()) {
            showAccessDenied("Payments");
            return;
        }
        loadView("views/payments.fxml", "Payments", "Track M-Pesa transactions and payment records");
        setActiveButton(paymentsBtn);
    }

    /**
     * Show Caretakers Management (LANDLORD only)
     */
    @FXML
    private void showCaretakers() {
        if (!session.isLandlord() && !session.isAdmin()) {
            showAccessDenied("Caretakers Management");
            return;
        }
        loadView("views/CaretakerManagement.fxml", "Caretakers", "Manage property caretakers");
        setActiveButton(caretakersBtn);
    }

    /**
     * Load a view into the content area
     * @param fxmlPath Path to the FXML file relative to resources
     * @param title Header title to display
     * @param subtitle Header subtitle to display
     */
    private void loadView(String fxmlPath, String title, String subtitle) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/westoncodeops/sample/" + fxmlPath));
            Parent view = loader.load();

            ScrollPane scrollPane = new ScrollPane(view);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setPannable(true);
            scrollPane.getStyleClass().add("content-scroll-pane");

            // Clear current content and add new view
            contentArea.getChildren().clear();
            contentArea.getChildren().add(scrollPane);

            // Update header
            headerTitle.setText(title);
            headerSubtitle.setText(subtitle);

        } catch (IOException e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
            
            // Show error message in content area
            Label errorLabel = new Label("Error loading view: " + fxmlPath + "\n" + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(errorLabel);
        }
    }

    /**
     * Set the active navigation button styling
     * @param button The button to mark as active
     */
    private void setActiveButton(Button button) {
        // Remove active class from previous button
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("active");
        }

        // Add active class to new button
        button.getStyleClass().add("active");
        currentActiveButton = button;
    }

    /**
     * Show access denied alert for unauthorized module access
     * @param moduleName The name of the module being accessed
     */
    private void showAccessDenied(String moduleName) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Access Denied");
        alert.setHeaderText("Insufficient Permissions");
        alert.setContentText("Your role (" + session.getRole() + ") does not have permission to access " +
                           moduleName + ".\n\nPlease contact your administrator if you believe this is an error.");
        alert.showAndWait();
    }
}

// Made with Bob
