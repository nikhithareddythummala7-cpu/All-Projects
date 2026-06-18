package com.example.table_booking.controller;

import com.example.table_booking.model.Review;
import com.example.table_booking.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class ReviewApiController {

    @Autowired
    private ReviewService reviewService;

    // ⭐ RETURN REVIEWS FOR A RESTAURANT (OWNER USES THIS)
    @GetMapping("/{restaurantId}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable String restaurantId) {
        List<Review> reviews = reviewService.getReviewsByRestaurantId(restaurantId);
        return ResponseEntity.ok(reviews);
    }
}
