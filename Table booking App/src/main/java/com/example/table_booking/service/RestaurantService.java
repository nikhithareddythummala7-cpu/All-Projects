package com.example.table_booking.service;

import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.User;
import com.example.table_booking.repository.RestaurantRepository;
import com.example.table_booking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TableService tableService;

    @Autowired
    private ReviewService reviewService;

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Optional<Restaurant> getRestaurantById(String id) {
        return restaurantRepository.findById(id);
    }

    public List<Restaurant> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return enrichRestaurantsWithOwnerFullName(restaurants);
    }

    public List<Restaurant> getActiveRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findByActiveTrue();
        return enrichRestaurantsWithOwnerFullName(restaurants);
    }

    public Restaurant updateRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(String id) {
        restaurantRepository.deleteById(id);
    }

    public List<Restaurant> searchRestaurants(String keyword) {
        // Implement search logic
        List<Restaurant> restaurants = restaurantRepository.findByNameContainingIgnoreCaseAndActiveTrue(keyword);
        return enrichRestaurantsWithOwnerFullName(restaurants);
    }

    public boolean verifyRestaurant(String id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if (restaurant.isPresent()) {
            Restaurant r = restaurant.get();
            r.setVerified(true);
            r.setActive(true);
            restaurantRepository.save(r);
            return true;
        }
        return false;
    }

    public List<com.example.table_booking.model.Table> getAvailableTables(String restaurantId) {
        return tableService.getTablesByRestaurantId(restaurantId);
    }

    public double getAverageRating(String restaurantId) {
        return reviewService.getAverageRatingForRestaurant(restaurantId);
    }

    public long getTotalReviews(String restaurantId) {
        return reviewService.getTotalReviewsForRestaurant(restaurantId);
    }

    public Optional<Restaurant> findRestaurantByOwner(String ownerEmail) {
        logger.info("Finding restaurant by owner email: {}", ownerEmail);
        try {
            Optional<Restaurant> restaurant = restaurantRepository.findByOwnerEmail(ownerEmail);
            if (restaurant.isPresent()) {
                logger.info("Restaurant found for owner {}: {}", ownerEmail, restaurant.get().getName());
            } else {
                logger.warn("No restaurant found for owner: {}", ownerEmail);
            }
            return restaurant;
        } catch (Exception e) {
            logger.error("Error finding restaurant by owner email: {}", ownerEmail, e);
            throw e;
        }
    }

    public Optional<Restaurant> updateRestaurantProfile(String ownerEmail, Restaurant updatedRestaurant) {
        logger.info("Updating restaurant profile for owner: {}", ownerEmail);
        try {
            Optional<Restaurant> existingOpt = restaurantRepository.findByOwnerEmail(ownerEmail);
            if (!existingOpt.isPresent()) {
                logger.warn("No restaurant found for owner: {}", ownerEmail);
                return Optional.empty();
            }

            Restaurant existing = existingOpt.get();
            logger.info("Updating restaurant: {} (ID: {})", existing.getName(), existing.getId());

            // Update only allowed fields
            existing.setName(updatedRestaurant.getName());
            existing.setAddress(updatedRestaurant.getAddress());
            existing.setCuisineType(updatedRestaurant.getCuisineType());
            existing.setDescription(updatedRestaurant.getDescription());
            existing.setPhoneNumber(updatedRestaurant.getPhoneNumber());
            existing.setOpeningTime(updatedRestaurant.getOpeningTime());
            existing.setClosingTime(updatedRestaurant.getClosingTime());
            existing.setPriceRange(updatedRestaurant.getPriceRange());

            Restaurant saved = restaurantRepository.save(existing);
            logger.info("Restaurant profile updated successfully: {} (ID: {})", saved.getName(), saved.getId());
            return Optional.of(saved);

        } catch (Exception e) {
            logger.error("Error updating restaurant profile for owner: {}", ownerEmail, e);
            throw e;
        }
    }

    public Restaurant getRestaurantByOwnerEmail(String ownerEmail) {
        logger.info("Getting restaurant by owner email: {}", ownerEmail);
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByOwnerEmail(ownerEmail);
        if (restaurantOpt.isEmpty()) {
            logger.error("No restaurant found for owner email: {}", ownerEmail);
            throw new RuntimeException("Restaurant not found for owner: " + ownerEmail);
        }
        return restaurantOpt.get();
    }

    private List<Restaurant> enrichRestaurantsWithOwnerFullName(List<Restaurant> restaurants) {
        return restaurants.stream().peek(restaurant -> {
            if (restaurant.getOwnerId() != null && !restaurant.getOwnerId().isEmpty()) {
                Optional<User> ownerOpt = userRepository.findById(restaurant.getOwnerId());
                ownerOpt.ifPresent(user -> restaurant.setOwnerFullName(user.getFullName()));
            }
        }).collect(Collectors.toList());
    }
}
