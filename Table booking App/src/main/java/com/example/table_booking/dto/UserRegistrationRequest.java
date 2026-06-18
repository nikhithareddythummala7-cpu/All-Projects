package com.example.table_booking.dto;

import com.example.table_booking.model.User;
import jakarta.validation.constraints.NotBlank;

public class UserRegistrationRequest {
    @NotBlank(message = "Username cannot be blank")

    private String username;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @NotBlank(message = "Email cannot be blank")
    private String email;

    private User.UserRole role;

    public User.UserRole getRole() {
        return role;
    }


    public void setRole(User.UserRole role) {
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
