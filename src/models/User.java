package models;

import java.util.Date;


public class User {

    protected String userId;
    protected String username;
    protected String password;
    protected String fullName;
    protected String email;
    protected String phone;
    protected String userType;
    protected Date createdDate;

    public User() {
        this.createdDate = new Date();
    }

    public User(String userId, String username, String password, String fullName,
                String email, String phone, String userType) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
        this.createdDate = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void displayInfo() {
        System.out.println("User: " + fullName + " (" + userType + ")");
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }

    public boolean isCustomer() {
        return "CUSTOMER".equals(userType);
    }

    public boolean isManager() {
        return "MANAGER".equals(userType);
    }

    public boolean isAdmin() {
        return "ADMIN".equals(userType);
    }
}
