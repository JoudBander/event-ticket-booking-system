package controllers;

import database.EventDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Event;
import models.User;
import helper.ValidationHelper;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CreateEventPageController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField eventNameField;

    @FXML
    private ComboBox<String> eventTypeCombo;

    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private TextField locationField;

    @FXML
    private TextField ticketPriceField;

    @FXML
    private TextField totalSeatsField;

    private User currentUser;
    private EventDB eventDAO;

    @FXML
    public void initialize() {
        eventDAO = new EventDB();
        eventTypeCombo.getItems().addAll("MATCH", "MOVIE", "THEATER");
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + "!");
    }

    @FXML
    private void handleCreateEvent() {
        if (!validateInputs()) {
            return;
        }

        String eventName = eventNameField.getText();
        String eventType = eventTypeCombo.getValue();
        LocalDate localDate = eventDatePicker.getValue();
        Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String location = locationField.getText();
        double ticketPrice = Double.parseDouble(ticketPriceField.getText());
        int totalSeats = Integer.parseInt(totalSeatsField.getText());

        Event event = new Event();
        event.setEventName(eventName);
        event.setEventType(eventType);
        event.setEventDate(eventDate);
        event.setLocation(location);
        event.setTicketPrice(ticketPrice);
        event.setTotalSeats(totalSeats);
        event.setAvailableSeats(totalSeats);
        event.setManagerId(currentUser.getUserId());

        try {
            eventDAO.insert(event);
            showSuccess("Event created successfully!");
            clearForm();
        } catch (SQLException e) {
            showError("Failed to create event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
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

    private void clearForm() {
        eventNameField.clear();
        eventTypeCombo.setValue(null);
        eventDatePicker.setValue(null);
        locationField.clear();
        ticketPriceField.clear();
        totalSeatsField.clear();
    }

    private boolean validateInputs() {
        String eventName = eventNameField.getText();
        if (!ValidationHelper.isValidEventName(eventName)) {
            showError("Event name must be at least 3 characters.");
            return false;
        }

        String eventType = eventTypeCombo.getValue();
        if (eventType == null) {
            showError("Please select an event type.");
            return false;
        }

        LocalDate localDate = eventDatePicker.getValue();
        if (localDate == null) {
            showError("Please select an event date.");
            return false;
        }

        Date eventDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (!ValidationHelper.isFutureDate(eventDate)) {
            showError(ValidationHelper.getFutureDateError());
            return false;
        }

        String location = locationField.getText();
        if (!ValidationHelper.isValidLocation(location)) {
            showError("Location must be at least 3 characters.");
            return false;
        }

        try {
            double price = Double.parseDouble(ticketPriceField.getText());
            if (!ValidationHelper.isValidPrice(price)) {
                showError(ValidationHelper.getPriceError());
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid price.");
            return false;
        }

        try {
            int seats = Integer.parseInt(totalSeatsField.getText());
            if (!ValidationHelper.isValidSeats(seats)) {
                showError(ValidationHelper.getSeatsError());
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return false;
        }

        return true;
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
