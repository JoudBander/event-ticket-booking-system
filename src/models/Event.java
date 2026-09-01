package models;

import interfaces.Searchable;
import java.util.Date;

public class Event implements Searchable {

    private String eventId;
    private String eventName;
    private String eventType;
    private Date eventDate;
    private String location;
    private double ticketPrice;
    private int totalSeats;
    private int availableSeats;
    private String managerId;
    private Date createdDate;

    public Event() {
        this.createdDate = new Date();
    }

    public Event(String eventId, String eventName, String eventType, Date eventDate,
                 String location, double ticketPrice, int totalSeats, String managerId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.location = location;
        this.ticketPrice = ticketPrice;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.managerId = managerId;
        this.createdDate = new Date();
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean hasAvailableSeats() {
        return availableSeats > 0;
    }

    public boolean hasAvailableSeats(int numSeats) {
        return availableSeats >= numSeats;
    }

    public boolean bookSeats(int numSeats) {
        if (hasAvailableSeats(numSeats)) {
            availableSeats -= numSeats;
            return true;
        }
        return false;
    }

    public void cancelSeats(int numSeats) {
        availableSeats += numSeats;
        if (availableSeats > totalSeats) {
            availableSeats = totalSeats;
        }
    }

    public int getBookedSeats() {
        return totalSeats - availableSeats;
    }

    public double getOccupancyPercentage() {
        if (totalSeats == 0) return 0;
        return (double) getBookedSeats() / totalSeats * 100;
    }

    public boolean isFutureEvent() {
        return eventDate.after(new Date());
    }

    public boolean isMatch() {
        return "MATCH".equals(eventType);
    }

    public boolean isMovie() {
        return "MOVIE".equals(eventType);
    }

    public boolean isTheaterShow() {
        return "THEATER".equals(eventType);
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventDate=" + eventDate +
                ", location='" + location + '\'' +
                ", ticketPrice=" + ticketPrice +
                ", availableSeats=" + availableSeats + "/" + totalSeats +
                '}';
    }

    public String getSummary() {
        return eventName + " (" + eventType + ") - " + location;
    }

    @Override
    public boolean matchesSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }

        String lowerKeyword = keyword.toLowerCase();

        if (eventName != null && eventName.toLowerCase().contains(lowerKeyword)) {
            return true;
        }

        if (eventType != null && eventType.toLowerCase().contains(lowerKeyword)) {
            return true;
        }

        if (location != null && location.toLowerCase().contains(lowerKeyword)) {
            return true;
        }

        return false;
    }

    @Override
    public String getSearchableText() {
        StringBuilder sb = new StringBuilder();

        if (eventName != null) {
            sb.append(eventName).append(" ");
        }
        if (eventType != null) {
            sb.append(eventType).append(" ");
        }
        if (location != null) {
            sb.append(location).append(" ");
        }

        return sb.toString().trim();
    }
}
