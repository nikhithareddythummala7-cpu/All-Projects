package com.example.table_booking.controller;

import com.example.table_booking.dto.UserRegistrationRequest;
import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.User;
import com.example.table_booking.service.RestaurantService;
import com.example.table_booking.service.UserService;
import com.example.table_booking.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/")
    public String home() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRegistrationRequest registrationRequest, 
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register"; // Return to registration page with validation errors
        }

        try {
            User user = new User();
            user.setUsername(registrationRequest.getUsername());
            user.setEmail(registrationRequest.getEmail().trim().toLowerCase());
            user.setPassword(registrationRequest.getPassword());
            user.setRole(registrationRequest.getRole());
            
            if (registrationRequest.getRole() == User.UserRole.RESTAURANT_OWNER) {
                user.setAccountVerified(false); // Restaurant owners need verification
            } else {
                user.setRole(User.UserRole.CUSTOMER);
                user.setAccountVerified(true); // Customers are verified by default
            }
            
            userService.createUser(user);
            return "redirect:/login?registration=success";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/api/user/role")
    @ResponseBody
    public ResponseEntity<?> getCurrentUserRole(HttpServletRequest request) {
        try {
            String jwt = getJwtFromRequest(request);
            if (jwt == null) return ResponseEntity.status(401).body("{\"error\": \"No JWT token\"}");

            String email = jwtUtil.extractUsername(jwt);
            User user = userService.getUserByEmail(email).orElse(null);

            if (user == null) {
                return ResponseEntity.status(404).body("{\"error\": \"User not found\"}");
            }

            return ResponseEntity.ok(new RoleResponse(user.getRole().toString()));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Internal server error\"}");
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // Inner class for role response
    public static class RoleResponse {
        private String role;

        public RoleResponse(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
