package helper;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class AlertHelper {

    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showSuccess(String message) {
        showInfo("Success", "Operation Successful", message);
    }

    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showError(String message) {
        showError("Error", "Operation Failed", message);
    }

    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showWarning(String message) {
        showWarning("Warning", "Please Note", message);
    }

    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static boolean showConfirmation(String message) {
        return showConfirmation("Confirmation", "Please Confirm", message);
    }

    public static void showDatabaseError(String operation, String errorMessage) {
        showError(
            "Database Error",
            operation + " Failed",
            "An error occurred while " + operation + ":\n" + errorMessage
        );
    }

    public static void showValidationError(String fieldName, String message) {
        showWarning(
            "Validation Error",
            fieldName + " is Invalid",
            message
        );
    }

    public static void showLoginError() {
        showError(
            "Login Failed",
            "Invalid Credentials",
            "Username or password is incorrect.\nPlease try again."
        );
    }

    public static void showBookingSuccess(String eventName, int numTickets, double totalAmount) {
        showSuccess(
            "Booking confirmed for: " + eventName + "\n" +
            "Number of tickets: " + numTickets + "\n" +
            "Total amount: " + String.format("%.2f SAR", totalAmount)
        );
    }

    public static void showInsufficientSeats(int available) {
        showWarning(
            "Insufficient Seats",
            "Not Enough Seats Available",
            "Only " + available + " seats are available for this event."
        );
    }

    public static boolean showDeleteConfirmation(String itemType, String itemName) {
        return showConfirmation(
            "Delete " + itemType,
            "Are you sure?",
            "Do you want to delete " + itemType + ": " + itemName + "?\n" +
            "This action cannot be undone."
        );
    }
}
