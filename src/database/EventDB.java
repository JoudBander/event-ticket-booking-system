package database;

import interfaces.DatabaseOperations;
import models.Event;
import java.sql.*;
import java.util.ArrayList;


public class EventDB implements DatabaseOperations<Event> {

    private String generateNextEventId() throws SQLException {
        String sql = "SELECT MAX(EVENT_ID) FROM EVENTS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null) {
                    lastId = lastId.trim();
                    int number = Integer.parseInt(lastId.substring(1));
                    return String.format("E%03d", number + 1);
                }
            }
            return "E001";

        } catch (SQLException e) {
            System.err.println("Error generating Event ID: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void insert(Event event) throws SQLException {
        if (event.getEventId() == null || event.getEventId().isEmpty()) {
            event.setEventId(generateNextEventId());
        }

        String sql = "INSERT INTO EVENTS (EVENT_ID, EVENT_NAME, EVENT_TYPE, EVENT_DATE, " +
                     "LOCATION, TICKET_PRICE, TOTAL_SEATS, AVAILABLE_SEATS, MANAGER_ID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getEventId());
            pstmt.setString(2, event.getEventName());
            pstmt.setString(3, event.getEventType());
            pstmt.setDate(4, new java.sql.Date(event.getEventDate().getTime()));
            pstmt.setString(5, event.getLocation());
            pstmt.setDouble(6, event.getTicketPrice());
            pstmt.setInt(7, event.getTotalSeats());
            pstmt.setInt(8, event.getAvailableSeats());
            pstmt.setString(9, event.getManagerId());

            pstmt.executeUpdate();

            System.out.println("Event inserted successfully: " + event.getEventName());

        } catch (SQLException e) {
            System.err.println("Error inserting event: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(Event event) throws SQLException {
        String sql = "UPDATE EVENTS SET EVENT_NAME = ?, EVENT_TYPE = ?, EVENT_DATE = ?, " +
                     "LOCATION = ?, TICKET_PRICE = ?, TOTAL_SEATS = ?, AVAILABLE_SEATS = ? " +
                     "WHERE EVENT_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, event.getEventName());
            pstmt.setString(2, event.getEventType());
            pstmt.setDate(3, new java.sql.Date(event.getEventDate().getTime()));
            pstmt.setString(4, event.getLocation());
            pstmt.setDouble(5, event.getTicketPrice());
            pstmt.setInt(6, event.getTotalSeats());
            pstmt.setInt(7, event.getAvailableSeats());
            pstmt.setString(8, event.getEventId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Event updated successfully: " + event.getEventName());
            } else {
                System.out.println("No event found with ID: " + event.getEventId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating event: " + e.getMessage());
            throw e;
        }
    }

    // Deletes related bookings first to avoid foreign key constraints
    @Override
    public void delete(String eventId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String deleteBookingsSql = "DELETE FROM BOOKINGS WHERE EVENT_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteBookingsSql)) {
                pstmt.setString(1, eventId);
                int bookingsDeleted = pstmt.executeUpdate();
                System.out.println("Deleted " + bookingsDeleted + " booking(s) for event: " + eventId);
            }

            String deleteEventSql = "DELETE FROM EVENTS WHERE EVENT_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteEventSql)) {
                pstmt.setString(1, eventId);
                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Event deleted successfully: " + eventId);
                } else {
                    System.out.println("No event found with ID: " + eventId);
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
            System.err.println("Error deleting event: " + e.getMessage());
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
    public Event findById(String eventId) throws SQLException {
        String sql = "SELECT * FROM EVENTS WHERE EVENT_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, eventId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractEventFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding event by ID: " + e.getMessage());
            throw e;
        }

        return null;
    }

    @Override
    public ArrayList<Event> findAll() throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM EVENTS ORDER BY EVENT_DATE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Event event = extractEventFromResultSet(rs);
                events.add(event);
            }

        } catch (SQLException e) {
            System.err.println("Error finding all events: " + e.getMessage());
            throw e;
        }

        return events;
    }

    public ArrayList<Event> findByType(String eventType) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM EVENTS WHERE EVENT_TYPE = ? ORDER BY EVENT_DATE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, eventType);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Event event = extractEventFromResultSet(rs);
                events.add(event);
            }

        } catch (SQLException e) {
            System.err.println("Error finding events by type: " + e.getMessage());
            throw e;
        }

        return events;
    }

    public ArrayList<Event> findByManager(String managerId) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM EVENTS WHERE MANAGER_ID = ? ORDER BY EVENT_DATE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, managerId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Event event = extractEventFromResultSet(rs);
                events.add(event);
            }

        } catch (SQLException e) {
            System.err.println("Error finding events by manager: " + e.getMessage());
            throw e;
        }

        return events;
    }

    public ArrayList<Event> findAvailableEvents() throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM EVENTS WHERE AVAILABLE_SEATS > 0 AND EVENT_DATE > SYSDATE " +
                     "ORDER BY EVENT_DATE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Event event = extractEventFromResultSet(rs);
                events.add(event);
            }

        } catch (SQLException e) {
            System.err.println("Error finding available events: " + e.getMessage());
            throw e;
        }

        return events;
    }

    public ArrayList<Event> searchEvents(String keyword) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM EVENTS WHERE " +
                     "UPPER(EVENT_NAME) LIKE ? OR " +
                     "UPPER(LOCATION) LIKE ? OR " +
                     "UPPER(EVENT_TYPE) LIKE ? " +
                     "ORDER BY EVENT_DATE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword.toUpperCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Event event = extractEventFromResultSet(rs);
                events.add(event);
            }

        } catch (SQLException e) {
            System.err.println("Error searching events: " + e.getMessage());
            throw e;
        }

        return events;
    }

    public void updateAvailableSeats(String eventId, int numSeats) throws SQLException {
        String sql = "UPDATE EVENTS SET AVAILABLE_SEATS = AVAILABLE_SEATS + ? WHERE EVENT_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, numSeats);
            pstmt.setString(2, eventId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating available seats: " + e.getMessage());
            throw e;
        }
    }

    public int getEventCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM EVENTS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting event count: " + e.getMessage());
            throw e;
        }

        return 0;
    }

    public int getEventCountByType(String eventType) throws SQLException {
        String sql = "SELECT COUNT(*) FROM EVENTS WHERE EVENT_TYPE = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, eventType);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting event count by type: " + e.getMessage());
            throw e;
        }

        return 0;
    }

    private Event extractEventFromResultSet(ResultSet rs) throws SQLException {
        Event event = new Event();

        event.setEventId(rs.getString("EVENT_ID"));
        event.setEventName(rs.getString("EVENT_NAME"));
        event.setEventType(rs.getString("EVENT_TYPE"));
        event.setEventDate(rs.getDate("EVENT_DATE"));
        event.setLocation(rs.getString("LOCATION"));
        event.setTicketPrice(rs.getDouble("TICKET_PRICE"));
        event.setTotalSeats(rs.getInt("TOTAL_SEATS"));
        event.setAvailableSeats(rs.getInt("AVAILABLE_SEATS"));
        event.setManagerId(rs.getString("MANAGER_ID"));

        return event;
    }
}
