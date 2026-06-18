package com.example.table_booking.dto;

public class AdminRestaurantDTO {
    private String id;
    private String name;
    private String address;
    private String ownerEmail;
    private boolean verified;
    private boolean active;

    public AdminRestaurantDTO() {}

    public AdminRestaurantDTO(String id, String name, String address, String ownerEmail, boolean verified, boolean active) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.ownerEmail = ownerEmail;
        this.verified = verified;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address == null ? "" : address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwnerEmail() {
        return ownerEmail == null ? "" : ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
