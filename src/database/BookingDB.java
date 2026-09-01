package database;

import interfaces.DatabaseOperations;
import models.Booking;
import java.sql.*;
import java.util.ArrayList;
public class BookingDB implements DatabaseOperations<Booking> {

    private String generateNextBookingId() throws SQLException {
        String sql = "SELECT MAX(BOOKING_ID) FROM BOOKINGS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null) {
                    lastId = lastId.trim();
                    int number = Integer.parseInt(lastId.substring(1));
                    return String.format("B%03d", number + 1);
                }
            }
            return "B001";

        } catch (SQLException e) {
            System.err.println("Error generating Booking ID: " + e.getMessage());
            throw e;
        }
    }

    // Also updates available seats in the event
    @Override
    public void insert(Booking booking) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (booking.getBookingId() == null || booking.getBookingId().isEmpty()) {
                booking.setBookingId(generateNextBookingId());
            }

            String sql1 = "INSERT INTO BOOKINGS (BOOKING_ID, CUSTOMER_ID, EVENT_ID, " +
                         "BOOKING_DATE, NUM_TICKETS, TOTAL_AMOUNT, STATUS) " +
                         "VALUES (?, ?, ?, SYSDATE, ?, ?, ?)";

            pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setString(1, booking.getBookingId());
            pstmt1.setString(2, booking.getCustomerId());
            pstmt1.setString(3, booking.getEventId());
            pstmt1.setInt(4, booking.getNumTickets());
            pstmt1.setDouble(5, booking.getTotalAmount());
            pstmt1.setString(6, booking.getStatus());

            pstmt1.executeUpdate();

            if ("CONFIRMED".equals(booking.getStatus())) {
                String sql2 = "UPDATE EVENTS SET AVAILABLE_SEATS = AVAILABLE_SEATS - ? " +
                             "WHERE EVENT_ID = ?";

                pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setInt(1, booking.getNumTickets());
                pstmt2.setString(2, booking.getEventId());

                pstmt2.executeUpdate();
            }

            conn.commit();
            System.out.println("Booking inserted successfully: " + booking.getBookingId());

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
            System.err.println("Error inserting booking: " + e.getMessage());
            throw e;

        } finally {
            try {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    @Override
    public void update(Booking booking) throws SQLException {
        String sql = "UPDATE BOOKINGS SET NUM_TICKETS = ?, TOTAL_AMOUNT = ?, STATUS = ? " +
                     "WHERE BOOKING_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, booking.getNumTickets());
            pstmt.setDouble(2, booking.getTotalAmount());
            pstmt.setString(3, booking.getStatus());
            pstmt.setString(4, booking.getBookingId());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Booking updated successfully: " + booking.getBookingId());
            } else {
                System.out.println("No booking found with ID: " + booking.getBookingId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(String bookingId)  {
        String sql = "DELETE FROM BOOKINGS WHERE BOOKING_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookingId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Booking deleted successfully: " + bookingId);
            } else {
                System.out.println("No booking found with ID: " + bookingId);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            
        }
    }

    @Override
    public Booking findById(String bookingId)  {
        String sql = "SELECT * FROM BOOKINGS WHERE BOOKING_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookingId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractBookingFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding booking by ID: " + e.getMessage());
          
        }

        return null;
    }

    @Override
    public ArrayList<Booking> findAll() throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM BOOKINGS ORDER BY BOOKING_DATE DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.err.println("Error finding all bookings: " + e.getMessage());
            throw e;
        }

        return bookings;
    }

    public ArrayList<Booking> findByCustomer(String customerId) throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String sql = "SELECT B.*, E.EVENT_NAME FROM BOOKINGS B " +
                     "JOIN EVENTS E ON B.EVENT_ID = E.EVENT_ID " +
                     "WHERE B.CUSTOMER_ID = ? ORDER BY B.BOOKING_DATE DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setEventName(rs.getString("EVENT_NAME"));
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.err.println("Error finding bookings by customer: " + e.getMessage());
            throw e;
        }

        return bookings;
    }

    public ArrayList<Booking> findByEvent(String eventId) throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM BOOKINGS WHERE EVENT_ID = ? ORDER BY BOOKING_DATE DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, eventId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.err.println("Error finding bookings by event: " + e.getMessage());
            throw e;
        }

        return bookings;
    }

    public void cancelBooking(String bookingId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sql0 = "SELECT * FROM BOOKINGS WHERE BOOKING_ID = ?";
            PreparedStatement pstmt0 = conn.prepareStatement(sql0);
            pstmt0.setString(1, bookingId);
            ResultSet rs = pstmt0.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Booking not found: " + bookingId);
            }

            String eventId = rs.getString("EVENT_ID");
            int numTickets = rs.getInt("NUM_TICKETS");
            String currentStatus = rs.getString("STATUS");

            pstmt0.close();

            if (!"CONFIRMED".equals(currentStatus)) {
                throw new SQLException("Booking is already cancelled");
            }

            String sql1 = "UPDATE BOOKINGS SET STATUS = 'CANCELLED' WHERE BOOKING_ID = ?";

            pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setString(1, bookingId);

            pstmt1.executeUpdate();

            String sql2 = "UPDATE EVENTS SET AVAILABLE_SEATS = AVAILABLE_SEATS + ? " +
                         "WHERE EVENT_ID = ?";

            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setInt(1, numTickets);
            pstmt2.setString(2, eventId);

            pstmt2.executeUpdate();

            conn.commit();
            System.out.println("Booking cancelled successfully: " + bookingId);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back");
                } catch (SQLException ex) {
                    System.err.println("Error rolling back: " + ex.getMessage());
                }
            }
            System.err.println("Error cancelling booking: " + e.getMessage());
            throw e;

        } finally {
            try {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    public ArrayList<Booking> findConfirmedBookings() throws SQLException {
        ArrayList<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM BOOKINGS WHERE STATUS = 'CONFIRMED' ORDER BY BOOKING_DATE DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.err.println("Error finding confirmed bookings: " + e.getMessage());
            throw e;
        }

        return bookings;
    }

    public int getBookingCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM BOOKINGS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting booking count: " + e.getMessage());
            throw e;
        }

        return 0;
    }

    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(TOTAL_AMOUNT) FROM BOOKINGS WHERE STATUS = 'CONFIRMED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
            throw e;
        }

        return 0.0;
    }

    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();

        booking.setBookingId(rs.getString("BOOKING_ID"));
        booking.setCustomerId(rs.getString("CUSTOMER_ID"));
        booking.setEventId(rs.getString("EVENT_ID"));
        booking.setBookingDate(rs.getDate("BOOKING_DATE"));
        booking.setNumTickets(rs.getInt("NUM_TICKETS"));
        booking.setTotalAmount(rs.getDouble("TOTAL_AMOUNT"));
        booking.setStatus(rs.getString("STATUS"));

        return booking;
    }
}
