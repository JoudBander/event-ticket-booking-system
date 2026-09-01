package controllers;

import database.BookingDB;
import database.EventDB;
import database.UserDB;
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


public class UserManagementPageController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalUsersLabel;

    @FXML
    private Label totalEventsLabel;

    @FXML
    private Label totalBookingsLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private ComboBox<String> userTypeCombo;

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> userIdCol;

    @FXML
    private TableColumn<User, String> usernameCol;

    @FXML
    private TableColumn<User, String> fullNameCol;

    @FXML
    private TableColumn<User, String> emailCol;

    @FXML
    private TableColumn<User, String> phoneCol;

    @FXML
    private TableColumn<User, String> userTypeCol;

    private User currentUser;
    private UserDB userDAO;
    private EventDB eventDAO;
    private BookingDB bookingDAO;
    private ObservableList<User> usersList;

    @FXML
    public void initialize() {
        try {
            userDAO = new UserDB();
            eventDAO = new EventDB();
            bookingDAO = new BookingDB();
            usersList = FXCollections.observableArrayList();

            userTypeCombo.getItems().addAll("All Users", "CUSTOMER", "MANAGER", "ADMIN");
            userTypeCombo.setValue("All Users");

            userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
            usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
            fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
            userTypeCol.setCellValueFactory(new PropertyValueFactory<>("userType"));

            usersTable.setItems(usersList);
        } catch (Exception e) {
            System.err.println("Error initializing UserManagementPage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getFullName() + "!");
        loadStatistics();
        loadAllUsers();
    }

    private void loadStatistics() {
        try {
            ArrayList<User> allUsers = userDAO.findAll();
            totalUsersLabel.setText(String.valueOf(allUsers.size()));

            ArrayList<Event> allEvents = eventDAO.findAll();
            totalEventsLabel.setText(String.valueOf(allEvents.size()));

            double totalRevenue = bookingDAO.getTotalRevenue();
            totalRevenueLabel.setText(String.format("%.2f SAR", totalRevenue));

            ArrayList<models.Booking> allBookings = bookingDAO.findAll();
            int confirmedCount = 0;
            for (models.Booking booking : allBookings) {
                if ("CONFIRMED".equals(booking.getStatus())) {
                    confirmedCount++;
                }
            }
            totalBookingsLabel.setText(String.valueOf(confirmedCount));

        } catch (SQLException e) {
            System.err.println("Error loading statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAllUsers() {
        try {
            ArrayList<User> users = userDAO.findAll();
            usersList.clear();
            usersList.addAll(users);
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        String userType = userTypeCombo.getValue();

        try {
            ArrayList<User> users;

            if ("All Users".equals(userType)) {
                users = userDAO.findAll();
            } else {
                users = userDAO.findByType(userType);
            }

            usersList.clear();
            usersList.addAll(users);

        } catch (SQLException e) {
            showError("Failed to load users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteUser() {
        User user = usersTable.getSelectionModel().getSelectedItem();

        if (user == null) {
            showError("Please select a user to delete.");
            return;
        }

        if (user.getUserId().equals(currentUser.getUserId())) {
            showError("You cannot delete your own account.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you want to delete user: " + user.getUsername() + "?\nThis action cannot be undone.");

        if (alert.showAndWait().get() != ButtonType.OK) {
            return;
        }

        try {
            userDAO.delete(user.getUserId());
            showSuccess("User deleted successfully!");
            loadStatistics();
            handleRefresh();
        } catch (SQLException e) {
            showError("Failed to delete: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminMainPage.fxml"));
            Parent root = loader.load();

            AdminMainPageController controller = loader.getController();
            controller.setCurrentUser(currentUser);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
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
