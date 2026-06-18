package com.streamy.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String role;
    private String subscriptionType;
}
