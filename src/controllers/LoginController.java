package controllers;

import database.UserDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import helper.AlertHelper;
import helper.ValidationHelper;

import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private UserDB userDB;

    @FXML
    public void initialize() {
        userDB = new UserDB();
        statusLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        statusLabel.setText("");

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (ValidationHelper.isEmpty(username)) {
            statusLabel.setText("Please enter username");
            return;
        }

        if (ValidationHelper.isEmpty(password)) {
            statusLabel.setText("Please enter password");
            return;
        }

        try {
            User user = userDB.validateLogin(username, password);

            if (user != null) {
                navigateToDashboard(user);
            } else {
                statusLabel.setText("Invalid username or password");
                System.out.println("Login failed for user: " + username);
            }

        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            System.err.println("Database error during login:");
            e.printStackTrace();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            System.err.println("Unexpected error during login:");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Register - Event Ticket Booking System");

        } catch (Exception e) {
            AlertHelper.showError("Navigation Error", "Failed to Load Registration Page", e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToDashboard(User user) {
        try {
            String fxmlFile = "";
            String title = "";

            if (user.isCustomer()) {
                fxmlFile = "/views/CustomerMainPage.fxml";
                title = "Customer Dashboard - Event Ticket Booking System";
            } else if (user.isManager()) {
                fxmlFile = "/views/ManagerMainPage.fxml";
                title = "Manager Dashboard - Event Ticket Booking System";
            } else if (user.isAdmin()) {
                fxmlFile = "/views/AdminMainPage.fxml";
                title = "Admin Dashboard - Event Ticket Booking System";
            } else {
                statusLabel.setText("Invalid user type");
                System.err.println("Invalid user type: " + user.getUserType());
                return;
            }

            System.out.println("Loading dashboard: " + fxmlFile);
            System.out.println("User type: " + user.getUserType());

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));

            if (loader.getLocation() == null) {
                statusLabel.setText("FXML file not found: " + fxmlFile);
                System.err.println("FXML file not found: " + fxmlFile);
                return;
            }

            Parent root = loader.load();
            System.out.println("✓ FXML loaded successfully");

            try {
                if (user.isCustomer()) {
                    CustomerMainPageController controller = loader.getController();
                    if (controller != null) {
                        controller.setCurrentUser(user);
                        System.out.println("✓ Customer controller initialized");
                    }
                } else if (user.isManager()) {
                    ManagerMainPageController controller = loader.getController();
                    if (controller != null) {
                        controller.setCurrentUser(user);
                        System.out.println("✓ Manager controller initialized");
                    }
                } else if (user.isAdmin()) {
                    AdminMainPageController controller = loader.getController();
                    if (controller != null) {
                        controller.setCurrentUser(user);
                        System.out.println("✓ Admin controller initialized");
                    }
                }
            } catch (Exception ce) {
                System.err.println("Error initializing controller: " + ce.getMessage());
                ce.printStackTrace();
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle(title);

            System.out.println("✓ Dashboard loaded successfully!");

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            System.err.println("Failed to load dashboard:");
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
