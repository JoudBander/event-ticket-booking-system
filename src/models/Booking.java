package models;

import java.util.Date;

public class Booking {

    private String bookingId;
    private String customerId;
    private String eventId;
    private String eventName;
    private Date bookingDate;
    private int numTickets;
    private double totalAmount;
    private String status;

    public Booking() {
        this.bookingDate = new Date();
        this.status = "CONFIRMED";
    }

    public Booking(String bookingId, String customerId, String eventId,
                   int numTickets, double totalAmount) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.eventId = eventId;
        this.bookingDate = new Date();
        this.numTickets = numTickets;
        this.totalAmount = totalAmount;
        this.status = "CONFIRMED";
    }

    public Booking(String bookingId, String customerId, String eventId,
                   Date bookingDate, int numTickets, double totalAmount, String status) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.eventId = eventId;
        this.bookingDate = bookingDate;
        this.numTickets = numTickets;
        this.totalAmount = totalAmount;
        this.status = status;
    }


    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getNumTickets() {
        return numTickets;
    }

    public void setNumTickets(int numTickets) {
        this.numTickets = numTickets;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void calculateTotalAmount(double ticketPrice) {
        this.totalAmount = ticketPrice * numTickets;
    }

    public void confirm() {
        this.status = "CONFIRMED";
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    public boolean isConfirmed() {
        return "CONFIRMED".equals(status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    public double getUnitPrice() {
        if (numTickets == 0) return 0;
        return totalAmount / numTickets;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", bookingDate=" + bookingDate +
                ", numTickets=" + numTickets +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }

    public String getSummary() {
        return "Booking #" + bookingId + " - " + numTickets + " ticket(s) - " +
               totalAmount + " SAR (" + status + ")";
    }
}
