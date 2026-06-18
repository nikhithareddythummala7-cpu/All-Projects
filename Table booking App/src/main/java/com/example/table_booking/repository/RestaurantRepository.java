package com.example.table_booking.repository;

import com.example.table_booking.model.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    List<Restaurant> findByActiveTrue();
    List<Restaurant> findByNameContainingIgnoreCaseAndActiveTrue(String name);
    Optional<Restaurant> findByOwnerId(String ownerId);
    Optional<Restaurant> findByOwnerEmail(String ownerEmail);
}
