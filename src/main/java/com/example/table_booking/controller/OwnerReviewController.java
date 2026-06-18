package com.example.table_booking.controller;

import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.User;
import com.example.table_booking.service.RestaurantService;
import com.example.table_booking.service.UserService;
import com.example.table_booking.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OwnerReviewController {

    @Autowired private UserService userService;
    @Autowired private RestaurantService restaurantService;
    @Autowired private JwtUtils jwtUtils;

    @GetMapping("/owner/reviews")
    public String ownerReviews(Model model, HttpServletRequest request) {

        // ⭐ FIXED: Use extractToken() properly
        String token = extractToken(request);
        if (token == null) {
            return "redirect:/login";
        }

        String username = jwtUtils.extractUsername(token);
        if (username == null) return "redirect:/login";

        User owner = userService.getUserByEmail(username).orElse(null);
        if (owner == null) return "redirect:/login";

        Restaurant restaurant = restaurantService
                .findRestaurantByOwner(owner.getEmail())
                .orElse(null);

        model.addAttribute("restaurant", restaurant);

        return "owner-reviews";
    }

    // ⭐ FIXED: Correct token extraction
    private String extractToken(HttpServletRequest request) {
        // 1. Authorization header
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }

        // 2. JWT Cookie (your login uses cookies)
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt")) {
                    return cookie.getValue();
                }
            }
        }

        // 3. Fallback: localStorage token sent from JS
        return null;
    }
}
