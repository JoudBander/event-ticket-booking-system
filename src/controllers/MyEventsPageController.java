package controllers;

import database.EventDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Event;
import models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class MyEventsPageController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Event> eventsTable;

    @FXML
    private TableColumn<Event, String> eventIdCol;

    @FXML
    private TableColumn<Event, String> eventNameCol;

    @FXML
    private TableColumn<Event, String> eventTypeCol;

    @FXML
    private TableColumn<Event, Date> eventDateCol;

    @FXML
    private TableColumn<Event, String> locationCol;

    @FXML
    private TableColumn<Event, Double> priceCol;

    @FXML
    private TableColumn<Event, Integer> totalSeatsCol;

    @FXML
    private TableColumn<Event, Integer> availableSeatsCol;

    private User currentUser;
    private EventDB eventDAO;
    private ObservableList<Event> eventsList;

    @FXML
    public void initialize() {
        try {
            eventDAO = new EventDB();
            eventsList = FXCollections.observableArrayList();

            eventIdCol.setCellValueFactory(new PropertyValueFactory<>("eventId"));
            eventNameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
            eventTypeCol.setCellValueFactory(new PropertyValueFactory<>("eventType"));
            eventDateCol.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            priceCol.setCellValueFactory(new PropertyValueFactory<>("ticketPrice"));
            totalSeatsCol.setCellValueFactory(new PropertyValueFactory<>("totalSeats"));
            availableSeatsCol.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

            eventsTable.setItems(eventsList);
        } catch (Exception e) {
            System.err.println("Error initializing MyEventsPage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + "!");
        loadMyEvents();
    }

    private void loadMyEvents() {
        try {
            ArrayList<Event> events = eventDAO.findByManager(currentUser.getUserId());
            eventsList.clear();
            eventsList.addAll(events);
        } catch (SQLException e) {
            System.err.println("Error loading events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteEvent() {
        Event event = eventsTable.getSelectionModel().getSelectedItem();

        if (event == null) {
            showError("Please select an event to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Event");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to delete: " + event.getEventName() + "?\nThis action cannot be undone.");

        if (alert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            eventDAO.delete(event.getEventId());
            showSuccess("Event deleted successfully!");
            loadMyEvents();
        } catch (SQLException e) {
            showError("Failed to delete: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadMyEvents();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ManagerMainPage.fxml"));
            Parent root = loader.load();

            ManagerMainPageController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("Manager Dashboard");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
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

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
