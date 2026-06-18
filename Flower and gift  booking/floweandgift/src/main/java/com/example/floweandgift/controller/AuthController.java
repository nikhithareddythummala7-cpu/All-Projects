package com.example.floweandgift.controller;

import com.example.floweandgift.model.User;
import com.example.floweandgift.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phone,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("\n========== REGISTRATION REQUEST ==========");
            System.out.println("Username: " + username);
            System.out.println("Email: " + email);
            System.out.println("Password: " + (password != null ? "***" : "null"));
            System.out.println("Full Name: " + fullName);
            System.out.println("Phone: " + phone);
            System.out.println("==========================================\n");
            
            // Validate input
            if (username == null || username.trim().isEmpty()) {
                model.addAttribute("error", "Username is required");
                model.addAttribute("user", new User());
                return "register";
            }
            if (email == null || email.trim().isEmpty()) {
                model.addAttribute("error", "Email is required");
                model.addAttribute("user", new User());
                return "register";
            }
            if (password == null || password.trim().isEmpty()) {
                model.addAttribute("error", "Password is required");
                model.addAttribute("user", new User());
                return "register";
            }
            if (fullName == null || fullName.trim().isEmpty()) {
                model.addAttribute("error", "Full name is required");
                model.addAttribute("user", new User());
                return "register";
            }
            
            // Create user object
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setFullName(fullName);
            user.setPhone(phone);
            
            System.out.println("User object created: " + user);
            
            User registeredUser = userService.registerUser(user);
            System.out.println("✓ User registered successfully!");
            System.out.println("User ID: " + registeredUser.getId());
            System.out.println("Username: " + registeredUser.getUsername());
            System.out.println("Email: " + registeredUser.getEmail());
            
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            System.err.println("✗ Validation error: " + e.getMessage());
            model.addAttribute("error", e.getMessage());
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPhone(phone);
            model.addAttribute("user", user);
            return "register";
        } catch (Exception e) {
            System.err.println("✗ Registration error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred during registration: " + e.getMessage());
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFullName(fullName);
            user.setPhone(phone);
            model.addAttribute("user", user);
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}
