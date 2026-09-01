package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.User;

public class CustomerMainPageController {

    @FXML
    private Label welcomeLabel;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + "!");
    }

    @FXML
    private void handleBrowseEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AvailableEventsPage.fxml"));
            Parent root = loader.load();

            AvailableEventsPageController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("Available Events");

        } catch (Exception e) {
            System.err.println("Error loading Available Events page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MyBookingsPage.fxml"));
            Parent root = loader.load();

            MyBookingsPageController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("My Bookings");

        } catch (Exception e) {
            System.err.println("Error loading My Bookings page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 500, 400);
            stage.setScene(scene);
            stage.setTitle("Login - Event Ticket Booking System");

        } catch (Exception e) {
            System.err.println("Error loading login page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
