package com.example.table_booking.controller;

import com.example.table_booking.model.Review;
import com.example.table_booking.model.User;
import com.example.table_booking.service.BookingService;
import com.example.table_booking.service.ReviewService;
import com.example.table_booking.service.UserService;
import com.example.table_booking.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Controller
@RequestMapping("/bookings")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;


    // ⭐ 1. REVIEW PAGE (HTML)
    @GetMapping("/{bookingId}/review")
    public String showReviewForm(@PathVariable String bookingId, Model model) {
        model.addAttribute("bookingId", bookingId);
        return "addReview";  // Loads addReview.html
    }


    // ⭐ 2. SUBMIT REVIEW
    @PostMapping("/review/submit")
    @ResponseBody
    public ResponseEntity<?> submitReview(
            @RequestParam String bookingId,
            @RequestParam int rating,
            @RequestParam String comment,
            HttpServletRequest request) {

        try {
            // Extract JWT token
            String token = extractToken(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Authentication required"));
            }

            String username = jwtUtils.extractUsername(token);
            User user = userService.getUserByEmail(username).orElseThrow();

            // Get restaurant from booking
            var booking = bookingService.getBookingById(bookingId).orElseThrow();

            // Create review
            Review review = new Review();
            review.setUserId(user.getId());
            review.setRestaurantId(booking.getRestaurantId());
            review.setRating(rating);
            review.setComment(comment);

            reviewService.createReview(review);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/bookings/my?reviewSuccess=true")
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Collections.singletonMap("error", "Review submission failed"));
        }
    }


    // Extract token (same as other controllers)
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
