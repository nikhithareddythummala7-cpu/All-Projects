package com.example.table_booking.service;

import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.Review;
import com.example.table_booking.repository.RestaurantRepository;
import com.example.table_booking.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Optional<Review> getReviewById(String id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getReviewsByRestaurantId(String restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }

    public List<Review> getReviewsByUserId(String userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getTopReviewsByRestaurantId(String restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByRatingDesc(restaurantId);
    }

    public Review createReview(Review review) {
        Review savedReview = reviewRepository.save(review);

        // Update restaurant average rating
        updateRestaurantAverageRating(review.getRestaurantId());

        return savedReview;
    }

    public Review updateReview(String id, Review review) {
        return reviewRepository.findById(id)
                .map(existingReview -> {
                    existingReview.setRating(review.getRating());
                    existingReview.setComment(review.getComment());
                    Review updatedReview = reviewRepository.save(existingReview);

                    // Update restaurant average rating
                    updateRestaurantAverageRating(review.getRestaurantId());

                    return updatedReview;
                })
                .orElse(null);
    }

    public boolean deleteReview(String id) {
        Optional<Review> reviewOpt = reviewRepository.findById(id);
        if (reviewOpt.isPresent()) {
            String restaurantId = reviewOpt.get().getRestaurantId();
            reviewRepository.deleteById(id);

            // Update restaurant average rating
            updateRestaurantAverageRating(restaurantId);

            return true;
        }
        return false;
    }

    private void updateRestaurantAverageRating(String restaurantId) {
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);
        if (!reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
            if (restaurantOpt.isPresent()) {
                Restaurant restaurant = restaurantOpt.get();
                restaurant.setAverageRating(averageRating);
                restaurantRepository.save(restaurant);
            }
        }
    }

    public double getAverageRatingForRestaurant(String restaurantId) {
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public long getTotalReviewsForRestaurant(String restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId).size();
    }
}
