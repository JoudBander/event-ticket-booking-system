package models;

import java.util.ArrayList;

public class EventManager extends User {

    private ArrayList<Event> events;

    public EventManager() {
        super();
        this.userType = "MANAGER";
        this.events = new ArrayList<>();
    }

    public EventManager(String userId, String username, String password, String fullName,
                        String email, String phone) {
        super(userId, username, password, fullName, email, phone, "MANAGER");
        this.events = new ArrayList<>();
    }

    public void addEvent(Event event) {
        if (event != null) {
            events.add(event);
        }
    }

    public void removeEvent(Event event) {
        if (event != null) {
            events.remove(event);
        }
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public int getEventCount() {
        return events.size();
    }

    public boolean hasEvents() {
        return !events.isEmpty();
    }

    public ArrayList<Event> getEventsByType(String eventType) {
        ArrayList<Event> filteredEvents = new ArrayList<>();
        for (Event event : events) {
            if (eventType.equals(event.getEventType())) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    public ArrayList<Event> getFutureEvents() {
        ArrayList<Event> futureEvents = new ArrayList<>();
        for (Event event : events) {
            if (event.isFutureEvent()) {
                futureEvents.add(event);
            }
        }
        return futureEvents;
    }

    public int getTotalCapacity() {
        int total = 0;
        for (Event event : events) {
            total += event.getTotalSeats();
        }
        return total;
    }

    public int getTotalAvailableSeats() {
        int total = 0;
        for (Event event : events) {
            total += event.getAvailableSeats();
        }
        return total;
    }

    public Event findEventById(String eventId) {
        for (Event event : events) {
            if (eventId.equals(event.getEventId())) {
                return event;
            }
        }
        return null;
    }

    @Override
    public void displayInfo() {
        System.out.println("Event Manager: " + fullName);
        System.out.println("Total Events: " + getEventCount());
        System.out.println("Total Capacity: " + getTotalCapacity() + " seats");
    }

    @Override
    public String toString() {
        return "EventManager{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", eventCount=" + getEventCount() +
                '}';
    }
}
