package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.User;


public class ManagerMainPageController {

    @FXML
    private Label welcomeLabel;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + "!");
    }

    @FXML
    private void handleCreateEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CreateEventPage.fxml"));
            Parent root = loader.load();

            CreateEventPageController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("Create Event");

        } catch (Exception e) {
            System.err.println("Error loading Create Event page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MyEventsPage.fxml"));
            Parent root = loader.load();

            MyEventsPageController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("My Events");

        } catch (Exception e) {
            System.err.println("Error loading My Events page: " + e.getMessage());
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
