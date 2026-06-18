package com.example.table_booking.model;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

    private String jwt;
    private String message;
    private boolean success;

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(String jwt, String message) {
        this.jwt = jwt;
        this.message = message;
        this.success = jwt != null;
    }

    public AuthenticationResponse(String jwt, String message, boolean success) {
        this.jwt = jwt;
        this.message = message;
        this.success = success;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
