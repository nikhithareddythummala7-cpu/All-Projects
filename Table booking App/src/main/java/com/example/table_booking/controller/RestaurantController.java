package com.example.table_booking.controller;

import com.example.table_booking.model.Booking;
import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.Table;
import com.example.table_booking.model.User;
import com.example.table_booking.service.RestaurantService;
import com.example.table_booking.service.TableService;
import com.example.table_booking.service.UserService;
import com.example.table_booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class RestaurantController {

    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    private final RestaurantService restaurantService;
    private final TableService tableService;
    private final UserService userService;
    private final BookingService bookingService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService,
                                TableService tableService,
                                UserService userService,
                                BookingService bookingService) {
        this.restaurantService = restaurantService;
        this.tableService = tableService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    // -------------------------
    // PUBLIC HTML PAGE — NO JWT
    // -------------------------
    @GetMapping("/restaurants/view")
    public String showRestaurantsPage(Model model) {
        List<Restaurant> restaurants = restaurantService.getActiveRestaurants();
        model.addAttribute("restaurants", restaurants);
        return "restaurant/restaurantList"; // loads restaurants.html
    }

    // -------------------------
    // PUBLIC API (JSON)
    // -------------------------
    @GetMapping("/api/restaurants")
    @ResponseBody
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    @GetMapping("/api/restaurants/list")
    @ResponseBody
    public List<Restaurant> getActiveRestaurants() {
        return restaurantService.getActiveRestaurants();
    }

    @GetMapping("/api/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable String id) {
        Optional<Restaurant> restaurant = restaurantService.getRestaurantById(id);
        return restaurant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // -------------------------
    // OWNER: Add Restaurant
   @PostMapping("/api/restaurants/add")
public String createRestaurant(@ModelAttribute Restaurant restaurant, Model model) {
    logger.info("Entering createRestaurant method");

    try {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.UserRole.RESTAURANT_OWNER) {
            model.addAttribute("errorMessage", "Only restaurant owners can create restaurants");
            model.addAttribute("restaurant", new Restaurant());
            return "restaurant/add";
        }

        Optional<Restaurant> existingRestaurant = restaurantService.findRestaurantByOwner(user.getEmail());
        if (existingRestaurant.isPresent()) {
            return "redirect:/owner/bookings";
        }

        if (restaurant.getName() == null || restaurant.getName().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Restaurant name is required");
            return "restaurant/add";
        }

        if (restaurant.getAddress() == null || restaurant.getAddress().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Restaurant address is required");
            return "restaurant/add";
        }

        if (restaurant.getPhoneNumber() == null || restaurant.getPhoneNumber().trim().isEmpty()) {
            model.addAttribute("errorMessage", "Restaurant phone number is required");
            return "restaurant/add";
        }

        // ---------------------------
        // FIX: Convert string → list
        // ---------------------------
        if (restaurant.getImages() != null && restaurant.getImages().size() == 1) {
            String input = restaurant.getImages().get(0);

            if (input != null && !input.trim().isEmpty()) {
                List<String> imageList = List.of(input.trim());
                restaurant.setImages(imageList);
            } else {
                restaurant.setImages(null);
            }
        }

        restaurant.setOwnerEmail(user.getEmail());
        restaurant.setActive(true);
        restaurant.setVerified(false);

        Restaurant savedRestaurant = restaurantService.createRestaurant(restaurant);

        user.setRestaurantId(savedRestaurant.getId());
        userService.updateUser(user);

        return "redirect:/owner/bookings?success=restaurant_created";

    } catch (Exception e) {
        logger.error("Error creating restaurant", e);
        model.addAttribute("errorMessage",
                "Failed to create restaurant: " + e.getMessage());
        return "restaurant/add";
    }
}

    // -------------------------
    // OWNER: Update Restaurant
    // -------------------------
    @PutMapping("/api/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable String id, @RequestBody Restaurant restaurant) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Restaurant> existing = restaurantService.getRestaurantById(id);
            if (!existing.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            if (!existing.get().getOwnerEmail().equals(user.getEmail())) {
                return ResponseEntity.status(403).build();
            }

            restaurant.setId(id);
            restaurant.setOwnerEmail(user.getEmail());
            Restaurant updatedRestaurant = restaurantService.updateRestaurant(restaurant);
            return ResponseEntity.ok(updatedRestaurant);

        } catch (Exception e) {
            logger.error("Error updating restaurant", e);
            return ResponseEntity.status(500).build();
        }
    }

    // -------------------------
    // OWNER: Delete Restaurant
    // -------------------------
    @DeleteMapping("/api/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Restaurant> existing = restaurantService.getRestaurantById(id);
            if (!existing.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            if (!existing.get().getOwnerEmail().equals(user.getEmail())) {
                return ResponseEntity.status(403).build();
            }

            restaurantService.deleteRestaurant(id);
            user.setRestaurantId(null);
            userService.updateUser(user);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            logger.error("Error deleting restaurant", e);
            return ResponseEntity.status(500).build();
        }
    }

    // -------------------------
    // OWNER: Table Management
    // -------------------------
    @GetMapping("/api/restaurants/{restaurantId}/tables")
    @ResponseBody
    public List<Table> getTablesByRestaurantId(@PathVariable String restaurantId) {
        return tableService.getTablesByRestaurantId(restaurantId);
    }

    @PostMapping("/api/restaurants/{restaurantId}/tables")
    @ResponseBody
    public ResponseEntity<Table> addTable(@PathVariable String restaurantId, @RequestBody Table table) {
        table.setRestaurantId(restaurantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.createTable(table));
    }

    @PutMapping("/api/restaurants/{restaurantId}/tables/{tableId}")
    @ResponseBody
    public ResponseEntity<Table> updateTable(
            @PathVariable String restaurantId,
            @PathVariable String tableId,
            @RequestBody Table table) {

        Table updatedTable = tableService.updateTable(tableId, table);
        if (updatedTable != null) return ResponseEntity.ok(updatedTable);
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/api/restaurants/{restaurantId}/tables/{tableId}")
    @ResponseBody
    public ResponseEntity<Void> deleteTable(@PathVariable String restaurantId, @PathVariable String tableId) {
        boolean deleted = tableService.deleteTable(tableId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // -------------------------
    // OWNER: Bookings
    // -------------------------
    @GetMapping("/api/restaurants/{restaurantId}/bookings")
    @ResponseBody
    public ResponseEntity<?> getBookingsByRestaurantId(@PathVariable String restaurantId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            String username = auth.getName();
            User user = userService.getUserByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole() != User.UserRole.RESTAURANT_OWNER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
            }

            Optional<Restaurant> restaurantOpt =
                    restaurantService.getRestaurantById(restaurantId);

            if (!restaurantOpt.isPresent()) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "Restaurant not found"));
            }

            Restaurant restaurant = restaurantOpt.get();
            if (!restaurant.getOwnerEmail().equals(user.getEmail())) {
                return ResponseEntity.status(403)
                        .body(Map.of("error", "You do not own this restaurant"));
            }

            List<Booking> bookings =
                    bookingService.getBookingsByRestaurantId(restaurantId);

            return ResponseEntity.ok(bookings);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to load bookings"));
        }
    }

    // -------------------------
    // ADD RESTAURANT PAGE
    // -------------------------
    @GetMapping("/restaurant/add")
    public String showAddRestaurantPage(Model model) {
        model.addAttribute("restaurant", new Restaurant());
        return "restaurant/add";
    }

    // -------------------------
    // OWNER: PROFILE GET
    // -------------------------
    @GetMapping("/api/restaurants/profile")
    @ResponseBody
    public ResponseEntity<?> getRestaurantProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            String username = auth.getName();
            User user = userService.getUserByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Restaurant> restaurantOpt =
                    restaurantService.findRestaurantByOwner(user.getEmail());

            if (!restaurantOpt.isPresent()) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "No restaurant found"));
            }

            return ResponseEntity.ok(restaurantOpt.get());

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to load profile"));
        }
    }

    // -------------------------
    // OWNER: PROFILE UPDATE
    // -------------------------
    @PutMapping("/api/restaurants/profile")
    @ResponseBody
    public ResponseEntity<?> updateRestaurantProfile(@RequestBody Restaurant updatedRestaurant) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            String username = auth.getName();
            User user = userService.getUserByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Restaurant> existingOpt =
                    restaurantService.findRestaurantByOwner(user.getEmail());

            if (existingOpt.isPresent() && !existingOpt.get().isVerified()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Restaurant must be verified"));
            }

            if (updatedRestaurant.getName() == null ||
                updatedRestaurant.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Restaurant name required"));
            }

            Optional<Restaurant> updatedOpt =
                    restaurantService.updateRestaurantProfile(user.getEmail(), updatedRestaurant);

            if (!updatedOpt.isPresent()) {
                return ResponseEntity.status(404)
                        .body(Map.of("message", "Restaurant not found"));
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Profile updated successfully",
                    "restaurant", updatedOpt.get()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to update profile"));
        }
    }
}
