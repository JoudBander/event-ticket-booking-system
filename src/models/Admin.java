package models;

import java.util.HashMap;

public class Admin extends User {

    public Admin() {
        super();
        this.userType = "ADMIN";
    }

    public Admin(String userId, String username, String password, String fullName,
                 String email, String phone) {
        super(userId, username, password, fullName, email, phone, "ADMIN");
    }

    public HashMap<String, Integer> generateStatistics() {
        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("Total Users", 0);
        stats.put("Total Customers", 0);
        stats.put("Total Managers", 0);
        stats.put("Total Events", 0);
        stats.put("Total Bookings", 0);
        stats.put("Confirmed Bookings", 0);
        stats.put("Cancelled Bookings", 0);
        return stats;
    }

    public HashMap<String, Double> generateRevenueStats() {
        HashMap<String, Double> revenueStats = new HashMap<>();
        revenueStats.put("Total Revenue", 0.0);
        revenueStats.put("Match Revenue", 0.0);
        revenueStats.put("Movie Revenue", 0.0);
        revenueStats.put("Theater Revenue", 0.0);
        return revenueStats;
    }

    public HashMap<String, Integer> generateEventTypeStats() {
        HashMap<String, Integer> eventTypeStats = new HashMap<>();
        eventTypeStats.put("Matches", 0);
        eventTypeStats.put("Movies", 0);
        eventTypeStats.put("Theater Shows", 0);
        return eventTypeStats;
    }

    public boolean canPerformAdminOperations() {
        return "ADMIN".equals(this.userType);
    }

    @Override
    public void displayInfo() {
        System.out.println("Administrator: " + fullName);
        System.out.println("Access Level: Full System Access");
        System.out.println("Can manage users, events, and system settings");
    }

    @Override
    public String toString() {
        return "Admin{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", userType='ADMIN'" +
                '}';
    }
}
