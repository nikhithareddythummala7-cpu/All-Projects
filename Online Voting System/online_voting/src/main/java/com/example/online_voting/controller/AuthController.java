package com.example.online_voting.controller;

import com.example.online_voting.model.User;
import com.example.online_voting.service.UserService;
import com.example.online_voting.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuditService auditService;
    
    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        return "login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerVoter(@ModelAttribute User user, Model model, HttpServletRequest request) {
        try {
            userService.registerVoter(user);
            auditService.logAction(user.getId(), "VOTER", "REGISTRATION", 
                    "Voter registered: " + user.getEmail(), request.getRemoteAddr());
            model.addAttribute("message", "Registration successful! You can now login.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user);
            return "register";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userService.findByEmail(email).orElse(null);
        
        if (user == null) {
            return "redirect:/login";
        }
        
        String role = user.getRole().name();
        if (role.equals("VOTER")) {
            return "redirect:/voter/dashboard";
        } else if (role.equals("ELECTION_OFFICER")) {
            return "redirect:/officer/dashboard";
        } else if (role.equals("ADMIN")) {
            return "redirect:/admin/dashboard";
        }
        
        return "redirect:/login";
    }
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
