package com.example.medlypharma.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping({"/", "/home", "/index"})
    public Object home(Model model, HttpServletRequest request) {
        // Log incoming request
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        try {
            // Check if user is authenticated
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getPrincipal().equals("anonymousUser")) {
                // User is logged in, redirect to dashboard
                logger.info("Authenticated user detected, redirecting to dashboard");
                return "redirect:/dashboard";
            }
            // User is not logged in, show home page
            logger.info("Unauthenticated user, serving index page");
            return "index";
        } catch (Exception e) {
            logger.error("Error processing home request: {}", e.getMessage(), e);
            // Check if client accepts JSON (API request)
            String accept = request.getHeader("Accept");
            if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
                // Return JSON response for API clients
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Welcome to Medly Pharma");
                response.put("status", "success");
                response.put("error", e.getMessage());
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
            } else {
                // Return error page for web clients
                model.addAttribute("error", e.getMessage());
                return "error";
            }
        }
    }
}
