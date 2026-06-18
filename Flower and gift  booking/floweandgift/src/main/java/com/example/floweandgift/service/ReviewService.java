package com.example.floweandgift.service;

import com.example.floweandgift.model.Review;
import com.example.floweandgift.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review addReview(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        review.setApproved(false); // Reviews need admin approval
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByProduct(String productId) {
        return reviewRepository.findByProductIdAndApproved(productId, true);
    }

    public List<Review> getReviewsByProductId(String productId) {
        return getReviewsByProduct(productId);
    }

    public List<Review> getAllReviewsByProduct(String productId) {
        return reviewRepository.findByProductId(productId);
    }

    public List<Review> getReviewsByUser(String userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getPendingReviews() {
        return reviewRepository.findByApproved(false);
    }

    public Optional<Review> approveReview(String reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            review.setApproved(true);
            return Optional.of(reviewRepository.save(review));
        }
        return Optional.empty();
    }

    public boolean deleteReview(String reviewId) {
        if (reviewRepository.existsById(reviewId)) {
            reviewRepository.deleteById(reviewId);
            return true;
        }
        return false;
    }

    public double getAverageRating(String productId) {
        List<Review> reviews = getReviewsByProductId(productId);
        if (reviews.isEmpty()) return 0.0;
        return reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    public int getReviewCount(String productId) {
        return getReviewsByProductId(productId).size();
    }
}
