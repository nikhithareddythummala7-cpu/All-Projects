package com.streamy.service.impl;

import com.streamy.dto.RegisterRequest;
import com.streamy.dto.LoginRequest;
import com.streamy.dto.AuthResponse;
import com.streamy.model.User;
import com.streamy.repository.UserRepository;
import com.streamy.security.JwtUtil;
import com.streamy.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.Instant;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Collections.singletonList("USER"))
                .subscriptionType("FREE")
                .createdAt(Instant.now())
                .build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(user);
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole("USER");
        response.setSubscriptionType("FREE");
        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtil.generateToken(user);
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setRole(user.getRoles().get(0));
        response.setSubscriptionType(user.getSubscriptionType());
        return response;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return null;
        return new com.streamy.security.CustomUserDetails(user);
    }
}
