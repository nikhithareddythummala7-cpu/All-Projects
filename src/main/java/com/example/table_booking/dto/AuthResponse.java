package com.example.table_booking.dto;

public class AuthResponse {

    private String jwt;
    private String message;
    private boolean success;

    public AuthResponse(String jwt, String message, boolean success) {
        this.jwt = jwt;
        this.message = message;
        this.success = success;
    }

    public String getJwt() {
        return jwt;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }
}
