package com.example.table_booking.repository;

import com.example.table_booking.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByRestaurantId(String restaurantId);

    List<Review> findByUserId(String userId);

    List<Review> findByRestaurantIdOrderByRatingDesc(String restaurantId);
}
