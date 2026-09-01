package models;

import java.util.ArrayList;

public class Customer extends User {

    private ArrayList<Booking> bookings;

    public Customer() {
        super();
        this.userType = "CUSTOMER";
        this.bookings = new ArrayList<>();
    }

    public Customer(String userId, String username, String password, String fullName,
                    String email, String phone) {
        super(userId, username, password, fullName, email, phone, "CUSTOMER");
        this.bookings = new ArrayList<>();
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            bookings.add(booking);
        }
    }

    public void removeBooking(Booking booking) {
        if (booking != null) {
            bookings.remove(booking);
        }
    }

    public ArrayList<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(ArrayList<Booking> bookings) {
        this.bookings = bookings;
    }

    public int getBookingCount() {
        return bookings.size();
    }

    public boolean hasBookings() {
        return !bookings.isEmpty();
    }

    public double getTotalSpent() {
        double total = 0;
        for (Booking booking : bookings) {
            if ("CONFIRMED".equals(booking.getStatus())) {
                total += booking.getTotalAmount();
            }
        }
        return total;
    }

    public ArrayList<Booking> getConfirmedBookings() {
        ArrayList<Booking> confirmed = new ArrayList<>();
        for (Booking booking : bookings) {
            if ("CONFIRMED".equals(booking.getStatus())) {
                confirmed.add(booking);
            }
        }
        return confirmed;
    }

    @Override
    public void displayInfo() {
        System.out.println("Customer: " + fullName);
        System.out.println("Total Bookings: " + getBookingCount());
        System.out.println("Total Spent: " + getTotalSpent() + " SAR");
    }

    @Override
    public String toString() {
        return "Customer{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", bookingCount=" + getBookingCount() +
                '}';
    }
}
