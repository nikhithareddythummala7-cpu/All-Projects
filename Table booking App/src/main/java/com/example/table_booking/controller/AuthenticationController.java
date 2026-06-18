package com.example.table_booking.controller;

import com.example.table_booking.model.AuthRequest;
import com.example.table_booking.model.AuthenticationResponse;
import com.example.table_booking.service.MyUserDetailsService;
import com.example.table_booking.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";  // must match login.html in templates
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            // Load user details
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

            // Generate JWT token
            final String jwt = jwtUtil.generateToken(userDetails);

            // Return authentication response
            return ResponseEntity.ok(new AuthenticationResponse(jwt, "Login successful", true));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new AuthenticationResponse(null, "Invalid email or password", false));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new AuthenticationResponse(null, "Authentication failed: " + e.getMessage(), false));
        }
    }
}
