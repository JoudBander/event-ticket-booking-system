package controllers;

import database.UserDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Admin;
import models.Customer;
import models.EventManager;
import models.User;
import helper.AlertHelper;
import helper.ValidationHelper;

import java.sql.SQLException;


public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<String> userTypeCombo;

    @FXML
    private Label statusLabel;

    private UserDB userDB;

    @FXML
    public void initialize() {
        userDB = new UserDB();
        statusLabel.setText("");

        userTypeCombo.getItems().addAll("CUSTOMER", "MANAGER", "ADMIN");
    }

    @FXML
    private void handleRegister() {
        statusLabel.setText("");

        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String userType = userTypeCombo.getValue();

        if (!validateInputs(username, password, confirmPassword, fullName, email, phone, userType)) {
            return;
        }

        User user = createUserObject(userType);
        if (user == null) {
            AlertHelper.showError("Invalid user type selected");
            return;
        }

        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setUserType(userType);

        try {
            userDB.insert(user);
            statusLabel.setText("Registration successful!");
            System.out.println("User registered successfully: " + username);

            handleBackToLogin();

        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            System.err.println("Database error during registration:");
            e.printStackTrace();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            System.err.println("Unexpected error during registration:");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - Event Ticket Booking System");

        } catch (Exception e) {
            AlertHelper.showError("Navigation Error", "Failed to Load Login Page", e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs(String username, String password, String confirmPassword,
                                    String fullName, String email, String phone, String userType) {

        if (!ValidationHelper.isValidUsername(username)) {
            AlertHelper.showValidationError("Username", ValidationHelper.getUsernameError());
            return false;
        }

        try {
            if (userDB.usernameExists(username)) {
                AlertHelper.showWarning("Username already exists. Please choose a different username.");
                return false;
            }
        } catch (SQLException e) {
            AlertHelper.showDatabaseError("checking username", e.getMessage());
            return false;
        }

        if (!ValidationHelper.isValidPassword(password)) {
            AlertHelper.showValidationError("Password", ValidationHelper.getPasswordError());
            return false;
        }

        if (!password.equals(confirmPassword)) {
            AlertHelper.showValidationError("Password", "Passwords do not match.");
            return false;
        }

        if (!ValidationHelper.isValidFullName(fullName)) {
            AlertHelper.showValidationError("Full Name", ValidationHelper.getFullNameError());
            return false;
        }

        if (!ValidationHelper.isValidEmail(email)) {
            AlertHelper.showValidationError("Email", ValidationHelper.getEmailError());
            return false;
        }

        if (!ValidationHelper.isValidPhone(phone)) {
            AlertHelper.showValidationError("Phone", ValidationHelper.getPhoneError());
            return false;
        }

        if (userType == null || userType.isEmpty()) {
            AlertHelper.showValidationError("User Type", "Please select a user type.");
            return false;
        }

        return true;
    }

    private User createUserObject(String userType) {
        if (userType == null) {
            return null;
        }

        switch (userType) {
            case "CUSTOMER":
                return new Customer();
            case "MANAGER":
                return new EventManager();
            case "ADMIN":
                return new Admin();
            default:
                return null;
        }
    }
}
