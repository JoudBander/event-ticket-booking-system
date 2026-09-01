package database;

import interfaces.DatabaseOperations;
import models.*;
import java.sql.*;
import java.util.ArrayList;


public class UserDB implements DatabaseOperations<User> {

    private String generateNextUserId() throws SQLException {
        String sql = "SELECT MAX(USER_ID) FROM USERS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null) {
                    lastId = lastId.trim();
                    int number = Integer.parseInt(lastId.substring(1));
                    return String.format("U%03d", number + 1);
                }
            }
            return "U001";

        } catch (SQLException e) {
            System.err.println("Error generating User ID: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void insert(User user) throws SQLException {
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            user.setUserId(generateNextUserId());
        }

        String sql = "INSERT INTO USERS (USER_ID, USERNAME, PASSWORD, FULL_NAME, " +
                     "EMAIL, PHONE, USER_TYPE) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getFullName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getPhone());
            pstmt.setString(7, user.getUserType());

            pstmt.executeUpdate();

            System.out.println("User inserted successfully: " + user.getUsername());

        } catch (SQLException e) {
            System.err.println("Error inserting user: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE USERS SET USERNAME = ?, PASSWORD = ?, FULL_NAME = ?, " +
                     "EMAIL = ?, PHONE = ? WHERE USER_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhone());
            pstmt.setString(6, user.getUserId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User updated successfully: " + user.getUsername());
            } else {
                System.out.println("No user found with ID: " + user.getUserId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            throw e;
        }
    }

    // Deletes related bookings and events first to avoid foreign key constraints
    @Override
    public void delete(String userId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String deleteBookingsSql = "DELETE FROM BOOKINGS WHERE CUSTOMER_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteBookingsSql)) {
                pstmt.setString(1, userId);
                pstmt.executeUpdate();
                System.out.println("Deleted bookings for user: " + userId);
            }

            String deleteEventsSql = "DELETE FROM EVENTS WHERE MANAGER_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteEventsSql)) {
                pstmt.setString(1, userId);
                pstmt.executeUpdate();
                System.out.println("Deleted events for user: " + userId);
            }

            String deleteUserSql = "DELETE FROM USERS WHERE USER_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteUserSql)) {
                pstmt.setString(1, userId);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User deleted successfully: " + userId);
                } else {
                    System.out.println("No user found with ID: " + userId);
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back due to error");
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
            System.err.println("Error deleting user: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public User findById(String userId) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            throw e;
        }

        return null;
    }

    @Override
    public ArrayList<User> findAll() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS ORDER BY USER_TYPE, FULL_NAME";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
            throw e;
        }

        return users;
    }

    public User validateLogin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error validating login: " + e.getMessage());
            throw e;
        }

        return null;
    }

    public ArrayList<User> findByType(String userType) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS WHERE USER_TYPE = ? ORDER BY FULL_NAME";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userType);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = extractUserFromResultSet(rs);
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Error finding users by type: " + e.getMessage());
            throw e;
        }

        return users;
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USERS WHERE USERNAME = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            throw e;
        }

        return false;
    }

    public int getUserCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM USERS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting user count: " + e.getMessage());
            throw e;
        }

        return 0;
    }

    // Creates appropriate User subclass based on USER_TYPE
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        String userType = rs.getString("USER_TYPE");
        User user;

        switch (userType) {
            case "CUSTOMER":
                user = new Customer();
                break;
            case "MANAGER":
                user = new EventManager();
                break;
            case "ADMIN":
                user = new Admin();
                break;
            default:
                user = new User();
        }

        user.setUserId(rs.getString("USER_ID"));
        user.setUsername(rs.getString("USERNAME"));
        user.setPassword(rs.getString("PASSWORD"));
        user.setFullName(rs.getString("FULL_NAME"));
        user.setEmail(rs.getString("EMAIL"));
        user.setPhone(rs.getString("PHONE"));
        user.setUserType(rs.getString("USER_TYPE"));

        return user;
    }
}
