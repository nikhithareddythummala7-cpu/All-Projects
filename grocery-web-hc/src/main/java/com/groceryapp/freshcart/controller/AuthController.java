package com.groceryapp.freshcart.controller;

import com.groceryapp.freshcart.dto.JwtResponse;
import com.groceryapp.freshcart.dto.LoginRequest;
import com.groceryapp.freshcart.dto.SignUpRequest;
import com.groceryapp.freshcart.model.Role;
import com.groceryapp.freshcart.model.User;
import com.groceryapp.freshcart.repository.UserRepository;
import com.groceryapp.freshcart.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    // ----------------- LOGIN -----------------
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

        if (user == null || !encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid credentials!"));
        }

        // Generate JWT token using username
        String jwt = jwtUtils.generateTokenFromUsername(user.getUsername());

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                )
        );
    }

    // ----------------- SIGNUP -----------------
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Email is already in use!"));
        }

        // Create new user's account with requested role (default USER)
        Role role = signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.USER;
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                role
        );

        userRepository.save(user);

        // Generate JWT token using username
        String jwt = jwtUtils.generateTokenFromUsername(user.getUsername());

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                )
        );
    }

    // ----------------- ERROR RESPONSE DTO -----------------
    private static record ErrorResponse(String error) {}
}
