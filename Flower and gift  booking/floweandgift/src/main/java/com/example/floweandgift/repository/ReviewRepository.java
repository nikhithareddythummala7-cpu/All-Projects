package com.example.floweandgift.repository;

import com.example.floweandgift.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByProductIdAndApproved(String productId, boolean approved);

    List<Review> findByProductId(String productId);

    List<Review> findByUserId(String userId);

    List<Review> findByApproved(boolean approved);
}
