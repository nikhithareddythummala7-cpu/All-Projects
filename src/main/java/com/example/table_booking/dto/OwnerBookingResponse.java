package com.example.table_booking.dto;

import java.time.LocalDateTime;

public class OwnerBookingResponse {
    private String id;
    private LocalDateTime bookingTime;
    private int numberOfGuests;
    private String status;
    private String tableId;
    private String customerName;

    public OwnerBookingResponse() {}

    public OwnerBookingResponse(String id, LocalDateTime bookingTime, int numberOfGuests, String status, String tableId, String customerName) {
        this.id = id;
        this.bookingTime = bookingTime;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.tableId = tableId;
        this.customerName = customerName;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
