package com.example.table_booking.controller;

import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.Table;
import com.example.table_booking.model.User;
import com.example.table_booking.repository.RestaurantRepository;
import com.example.table_booking.repository.TableRepository;
import com.example.table_booking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/restaurant")
public class TableController {

    private static final Logger logger = LoggerFactory.getLogger(TableController.class);

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/tables")
    public String getRestaurantTables(Model model) {
        try {
            logger.info("Request received for restaurant tables");

            // Get logged-in restaurant owner via Principal
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                logger.warn("User not authenticated for restaurant tables");
                model.addAttribute("errorMessage", "User not authenticated");
                return "restaurant/tables";
            }

            logger.info("Logged-in user: {}", auth.getName());
            
            // Get the user and their restaurant
            Optional<User> userOpt = userService.getUserByEmail(auth.getName());
            if (userOpt.isEmpty()) {
                model.addAttribute("errorMessage", "User not found");
                return "restaurant/tables";
            }

            Optional<Restaurant> restaurantOpt = restaurantRepository.findByOwnerEmail(auth.getName());
            if (restaurantOpt.isEmpty()) {
                model.addAttribute("errorMessage", "No restaurant found for this user");
                return "restaurant/tables";
            }

            Restaurant restaurant = restaurantOpt.get();
            
            // Check if restaurant is verified
            if (!restaurant.isVerified()) {
                model.addAttribute("pendingMessage", "Your restaurant is pending verification");
                model.addAttribute("restaurant", restaurant);
                return "restaurant/tables";
            }

            // Get all tables for this restaurant
            List<Table> tablesList = tableRepository.findByRestaurantId(restaurant.getId());

            model.addAttribute("restaurant", restaurant);
            model.addAttribute("tables", tablesList);
            model.addAttribute("newTable", new Table()); // For the add table form

            logger.info("Retrieved {} tables for restaurant: {}", tablesList.size(), restaurant.getId());
            return "restaurant/tables";

        } catch (Exception e) {
            logger.error("Error retrieving restaurant tables", e);
            model.addAttribute("errorMessage", "An unexpected error occurred while retrieving tables.");
            return "restaurant/tables";
        }
    }

    @GetMapping("/profile")
    public String getRestaurantProfile(Model model) {
        try {
            logger.info("Request received for restaurant profile");

            // Get logged-in restaurant owner via Principal
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                logger.warn("User not authenticated for restaurant profile");
                // Allow access but show empty profile or redirect message
                model.addAttribute("errorMessage", "Please log in to view your restaurant profile.");
                return "restaurant/profile";
            }

            logger.info("Logged-in user: {}", auth.getName());

            // Find restaurant by owner email
            Optional<Restaurant> restaurantOpt = restaurantRepository.findByOwnerEmail(auth.getName());
            if (!restaurantOpt.isPresent()) {
                logger.warn("Restaurant not found for user: {}", auth.getName());
                model.addAttribute("errorMessage", "Restaurant not found for the logged-in owner.");
                return "restaurant/profile";
            }

            Restaurant restaurant = restaurantOpt.get();
            logger.info("Restaurant found: {} (ID: {})", restaurant.getName(), restaurant.getId());

            model.addAttribute("restaurant", restaurant);
            return "restaurant/profile";

        } catch (Exception e) {
            logger.error("Error retrieving restaurant profile", e);
            model.addAttribute("errorMessage", "An unexpected error occurred while retrieving the profile.");
            return "restaurant/profile";
        }
    }
}
