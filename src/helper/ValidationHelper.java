package helper;

import java.util.Date;

public class ValidationHelper {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(gmail\\.com|hotmail\\.com|outlook\\.com|yahoo\\.com)$";
    private static final String PHONE_PATTERN = "^[0-9]{10}$";
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_]{3,20}$";

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static boolean isValidUsername(String username) {
        if (isEmpty(username)) {
            return false;
        }
        return username.matches(USERNAME_PATTERN);
    }

    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        return password.length() >= 6;
    }

    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return email.matches(EMAIL_PATTERN);
    }

    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        return phone.matches(PHONE_PATTERN);
    }

    public static boolean isValidFullName(String fullName) {
        if (isEmpty(fullName)) {
            return false;
        }
        return fullName.length() >= 2 && fullName.matches("^[A-Za-z\\s\\u0600-\\u06FF]+$");
    }

    public static boolean isValidEventName(String eventName) {
        if (isEmpty(eventName)) {
            return false;
        }
        return eventName.length() >= 3;
    }

    public static boolean isValidLocation(String location) {
        if (isEmpty(location)) {
            return false;
        }
        return location.length() >= 3;
    }

    public static boolean isValidPrice(double price) {
        return price > 0;
    }

    public static boolean isValidSeats(int seats) {
        return seats > 0;
    }

    public static boolean isValidNumTickets(int numTickets) {
        return numTickets >= 1;
    }

    public static boolean isFutureDate(Date date) {
        if (date == null) {
            return false;
        }
        return date.after(new Date());
    }

    public static boolean isValidEventType(String eventType) {
        if (isEmpty(eventType)) {
            return false;
        }
        return "MATCH".equals(eventType) ||
               "MOVIE".equals(eventType) ||
               "THEATER".equals(eventType);
    }

    public static boolean isValidUserType(String userType) {
        if (isEmpty(userType)) {
            return false;
        }
        return "CUSTOMER".equals(userType) ||
               "MANAGER".equals(userType) ||
               "ADMIN".equals(userType);
    }

    public static boolean isValidBookingStatus(String status) {
        if (isEmpty(status)) {
            return false;
        }
        return "CONFIRMED".equals(status) || "CANCELLED".equals(status);
    }

    public static String getUsernameError() {
        return "Username must be 3-20 characters long and contain only letters, numbers, and underscores.";
    }

    public static String getPasswordError() {
        return "Password must be at least 6 characters long.";
    }

    public static String getEmailError() {
        return "Please enter a valid email address with domain: @gmail.com, @hotmail.com, @outlook.com, or @yahoo.com";
    }

    public static String getPhoneError() {
        return "Phone number must be 10 digits (e.g., 0501234567).";
    }

    public static String getFullNameError() {
        return "Full name must be at least 2 characters and contain only letters and spaces.";
    }

    public static String getPriceError() {
        return "Price must be a positive number.";
    }

    public static String getSeatsError() {
        return "Number of seats must be a positive number.";
    }

    public static String getFutureDateError() {
        return "Event date must be in the future.";
    }
}
