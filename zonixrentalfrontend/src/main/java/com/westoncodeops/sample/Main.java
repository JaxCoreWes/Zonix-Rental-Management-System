package com.westoncodeops.sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main Application Entry Point for Zonix Rental Property Management System
 * Launches the JavaFX application with the main shell layout
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Launch the login view first (non-maximized). Main shell will open after successful authentication.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/westoncodeops/sample/views/Welcome.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 540, 380);

        primaryStage.setTitle("Zonix Rental");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Application entry point
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
