package com.example.table_booking.dto;

import java.time.LocalDateTime;

public class AdminBookingDTO {
    private String id;
    private String userName;
    private String userEmail;
    private String restaurantName;
    private int tableNumber;
    private int numberOfGuests;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public AdminBookingDTO() {}

    public AdminBookingDTO(String id, String userName, String userEmail, String restaurantName, 
                          int tableNumber, int numberOfGuests, LocalDateTime startTime, 
                          LocalDateTime endTime, String status) {
        this.id = id;
        this.userName = userName == null ? "" : userName;
        this.userEmail = userEmail == null ? "" : userEmail;
        this.restaurantName = restaurantName == null ? "" : restaurantName;
        this.tableNumber = tableNumber;
        this.numberOfGuests = numberOfGuests;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status == null ? "" : status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
