package com.streamy.service;

import com.streamy.dto.RegisterRequest;
import com.streamy.dto.LoginRequest;
import com.streamy.dto.AuthResponse;
import com.streamy.model.User;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    org.springframework.security.core.userdetails.UserDetails getUserByEmail(String email);
}
