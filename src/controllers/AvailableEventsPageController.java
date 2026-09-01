package controllers;

import database.EventDB;
import database.BookingDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Booking;
import models.Event;
import models.User;
import helper.ValidationHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class AvailableEventsPageController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> eventTypeCombo;

    @FXML
    private TableView<Event> eventsTable;

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
    private TableColumn<Event, Integer> availableSeatsCol;

    @FXML
    private TextField numTicketsField;

    private User currentUser;
    private EventDB eventDAO;
    private BookingDB bookingDAO;
    private ObservableList<Event> eventsList;

    @FXML
    public void initialize() {
        try {
            eventDAO = new EventDB();
            bookingDAO = new BookingDB();
            eventsList = FXCollections.observableArrayList();

            eventTypeCombo.getItems().addAll("All Events", "MATCH", "MOVIE", "THEATER");
            eventTypeCombo.setValue("All Events");

            eventNameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
            eventTypeCol.setCellValueFactory(new PropertyValueFactory<>("eventType"));
            eventDateCol.setCellValueFactory(new PropertyValueFactory<>("eventDate"));
            locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
            priceCol.setCellValueFactory(new PropertyValueFactory<>("ticketPrice"));
            availableSeatsCol.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

            eventsTable.setItems(eventsList);
        } catch (Exception e) {
            System.err.println("Error initializing AvailableEventsPage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + "!");
        loadAvailableEvents();
    }

    private void loadAvailableEvents() {
        try {
            ArrayList<Event> events = eventDAO.findAvailableEvents();
            eventsList.clear();
            eventsList.addAll(events);
        } catch (SQLException e) {
            System.err.println("Error loading events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        String eventType = eventTypeCombo.getValue();

        try {
            ArrayList<Event> events;

            if ("All Events".equals(eventType)) {
                if (ValidationHelper.isEmpty(keyword)) {
                    events = eventDAO.findAvailableEvents();
                } else {
                    events = eventDAO.searchEvents(keyword);
                }
            } else {
                events = eventDAO.findByType(eventType);
                if (ValidationHelper.isNotEmpty(keyword)) {
                    ArrayList<Event> filtered = new ArrayList<>();
                    for (Event event : events) {
                        if (event.matchesSearch(keyword)) {
                            filtered.add(event);
                        }
                    }
                    events = filtered;
                }
            }

            eventsList.clear();
            eventsList.addAll(events);
        } catch (SQLException e) {
            System.err.println("Error searching events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        searchField.clear();
        eventTypeCombo.setValue("All Events");
        loadAvailableEvents();
    }

    @FXML
    private void handleBookEvent() {
        Event selectedEvent = eventsTable.getSelectionModel().getSelectedItem();

        if (selectedEvent == null) {
            showError("Please select an event to book.");
            return;
        }

        String numTicketsStr = numTicketsField.getText();
        int numTickets;

        try {
            numTickets = Integer.parseInt(numTicketsStr);
        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
            return;
        }

        if (!ValidationHelper.isValidNumTickets(numTickets)) {
            showError("Number of tickets must be at least 1.");
            return;
        }

        if (numTickets > selectedEvent.getAvailableSeats()) {
            showError("Only " + selectedEvent.getAvailableSeats() + " seats available.");
            return;
        }

        double totalAmount = numTickets * selectedEvent.getTicketPrice();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Booking");
        alert.setHeaderText("Book " + numTickets + " ticket(s)?");
        alert.setContentText("Event: " + selectedEvent.getEventName() + "\nTotal: " + String.format("%.2f SAR", totalAmount));

        if (alert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        Booking booking = new Booking();
        booking.setCustomerId(currentUser.getUserId());
        booking.setEventId(selectedEvent.getEventId());
        booking.setNumTickets(numTickets);
        booking.setTotalAmount(totalAmount);
        booking.setStatus("CONFIRMED");

        try {
            bookingDAO.insert(booking);
            showSuccess("Booking confirmed successfully!");
            loadAvailableEvents();
            numTicketsField.clear();
        } catch (SQLException e) {
            showError("Failed to book: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CustomerMainPage.fxml"));
            Parent root = loader.load();

            CustomerMainPageController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("Customer Dashboard");
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
