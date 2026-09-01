package controllers;

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
import models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class MyBookingsPageController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Booking> bookingsTable;

    @FXML
    private TableColumn<Booking, String> bookingIdCol;

    @FXML
    private TableColumn<Booking, String> eventNameCol;

    @FXML
    private TableColumn<Booking, Date> bookingDateCol;

    @FXML
    private TableColumn<Booking, Integer> numTicketsCol;

    @FXML
    private TableColumn<Booking, Double> totalAmountCol;

    @FXML
    private TableColumn<Booking, String> statusCol;

    private User currentUser;
    private BookingDB bookingDAO;
    private ObservableList<Booking> bookingsList;

    @FXML
    public void initialize() {
        try {
            bookingDAO = new BookingDB();
            bookingsList = FXCollections.observableArrayList();

            bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
            eventNameCol.setCellValueFactory(new PropertyValueFactory<>("eventName"));
            bookingDateCol.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
            numTicketsCol.setCellValueFactory(new PropertyValueFactory<>("numTickets"));
            totalAmountCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

            bookingsTable.setItems(bookingsList);
        } catch (Exception e) {
            System.err.println("Error initializing MyBookingsPage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + "!");
        loadMyBookings();
    }

    private void loadMyBookings() {
        try {
            ArrayList<Booking> bookings = bookingDAO.findByCustomer(currentUser.getUserId());
            bookingsList.clear();
            bookingsList.addAll(bookings);
        } catch (SQLException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();

        if (selectedBooking == null) {
            showError("Please select a booking to cancel.");
            return;
        }

        if ("CANCELLED".equals(selectedBooking.getStatus())) {
            showError("This booking is already cancelled.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to cancel this booking?\nBooking ID: " + selectedBooking.getBookingId());

        if (alert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            bookingDAO.cancelBooking(selectedBooking.getBookingId());
            showSuccess("Booking cancelled successfully.");
            loadMyBookings();
        } catch (SQLException e) {
            showError("Failed to cancel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadMyBookings();
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
