package com.westoncodeops.sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class WelcomeController {

    @FXML private Button signInBtn;
    @FXML private Button registerBtn;

    @FXML
    private void openSignIn() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/westoncodeops/sample/views/login.fxml"));
            Stage stage = (Stage) signInBtn.getScene().getWindow();
            stage.getScene().setRoot(loginRoot);
            stage.setTitle("Sign In");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openRegister() {
        try {
            Parent regRoot = FXMLLoader.load(getClass().getResource("/com/westoncodeops/sample/views/register.fxml"));
            Stage stage = (Stage) registerBtn.getScene().getWindow();
            stage.getScene().setRoot(regRoot);
            stage.setTitle("Register Landlord");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
