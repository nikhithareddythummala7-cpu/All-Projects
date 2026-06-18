package com.example.table_booking.dto;

public class BookingDTO {
    private String id;
    private String tableId;
    private String restaurantId;
    private String startTime;
    private String endTime;
    private int numberOfGuests;
    private String status;

    public BookingDTO() {}

    public BookingDTO(String id, String tableId, String restaurantId, String startTime, String endTime, int numberOfGuests, String status) {
        this.id = id;
        this.tableId = tableId;
        this.restaurantId = restaurantId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTableId() { return tableId; }
    public void setTableId(String tableId) { this.tableId = tableId; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
