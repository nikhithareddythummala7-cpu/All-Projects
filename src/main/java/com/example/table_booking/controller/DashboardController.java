package com.example.table_booking.controller;

import com.example.table_booking.model.User;
import com.example.table_booking.model.Booking;
import com.example.table_booking.model.Restaurant;
import com.example.table_booking.service.UserService;
import com.example.table_booking.service.RestaurantService;
import com.example.table_booking.service.BookingService;
import com.example.table_booking.service.TableService;
import com.example.table_booking.service.ReviewService;
import com.example.table_booking.service.MyUserDetailsService;
import com.example.table_booking.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Optional;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TableService tableService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtil;

    @GetMapping("/customer/dashboard")
    public String customerDashboard(Model model) {
        // Return template without authentication check - client-side JS will handle auth
        return "dashboard/customer";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        // Return template without authentication check - client-side JS will handle auth
        return "dashboard/admin";
    }



    @GetMapping("/owner/dashboard")
    public String ownerDashboard(Model model) {
        // Return template without authentication check - client-side JS will handle auth
        return "dashboard/restaurant";
    }

    // API endpoints for dashboard data
    @GetMapping("/api/dashboard/admin/stats")
    @ResponseBody
    public ResponseEntity<?> getAdminStats(HttpServletRequest request) {
        try {
            String jwt = getJwtFromRequest(request);
            if (jwt == null) return ResponseEntity.status(401).body("{\"error\": \"No JWT token\"}");

            String email = jwtUtil.extractUsername(jwt);
            User user = userService.getUserByEmail(email).orElse(null);

            if (user == null || user.getRole() != User.UserRole.ADMIN) {
                return ResponseEntity.status(403).body("{\"error\": \"Access denied\"}");
            }

            return ResponseEntity.ok(new AdminStats(
                userService.getAllUsers().size(),
                restaurantService.getAllRestaurants().size(),
                bookingService.getAllBookings().size(),
                restaurantService.getAllRestaurants().stream()
                    .filter(r -> !r.isVerified())
                    .toList().size()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Internal server error\"}");
        }
    }

    @GetMapping("/api/dashboard/restaurant/stats")
    @ResponseBody
    public ResponseEntity<?> getRestaurantStats(HttpServletRequest request) {
        try {
            String jwt = getJwtFromRequest(request);
            if (jwt == null) return ResponseEntity.status(401).body("{\"error\": \"No JWT token\"}");

            String email = jwtUtil.extractUsername(jwt);
            User user = userService.getUserByEmail(email).orElse(null);

            if (user == null || user.getRole() != User.UserRole.RESTAURANT_OWNER) {
                return ResponseEntity.status(403).body("{\"error\": \"Access denied\"}");
            }

            Restaurant restaurant = restaurantService.findRestaurantByOwner(user.getEmail()).orElse(null);
            if (restaurant == null) {
                return ResponseEntity.ok(Map.of("message", "NO_RESTAURANT"));
            }

            return ResponseEntity.ok(new RestaurantStats(
                bookingService.getBookingsByRestaurantId(restaurant.getId()).size(),
                tableService.getTablesByRestaurantId(restaurant.getId()).size(),
                reviewService.getAverageRatingForRestaurant(restaurant.getId()),
                reviewService.getTotalReviewsForRestaurant(restaurant.getId())
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Internal server error\"}");
        }
    }

    @GetMapping("/api/user/has-restaurant")
    @ResponseBody
    public ResponseEntity<?> hasRestaurant(HttpServletRequest request) {
        try {
            String jwt = getJwtFromRequest(request);
            if (jwt == null) return ResponseEntity.status(401).body("{\"error\": \"No JWT token\"}");

            String email = jwtUtil.extractUsername(jwt);
            User user = userService.getUserByEmail(email).orElse(null);

            if (user == null || user.getRole() != User.UserRole.RESTAURANT_OWNER) {
                return ResponseEntity.status(403).body("{\"error\": \"Access denied\"}");
            }

            boolean hasRestaurant = restaurantService.findRestaurantByOwner(user.getEmail()).isPresent();
            return ResponseEntity.ok(Map.of("hasRestaurant", hasRestaurant));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Internal server error\"}");
        }
    }

    @GetMapping("/api/dashboard/customer/info")
    @ResponseBody
    public ResponseEntity<?> getCustomerInfo(HttpServletRequest request) {
        try {
            String jwt = getJwtFromRequest(request);
            if (jwt == null) return ResponseEntity.status(401).body("{\"error\": \"No JWT token\"}");

            String email = jwtUtil.extractUsername(jwt);
            User user = userService.getUserByEmail(email).orElse(null);

            if (user == null || user.getRole() != User.UserRole.CUSTOMER) {
                return ResponseEntity.status(403).body("{\"error\": \"Access denied\"}");
            }

            // Get user's bookings
            List<com.example.table_booking.model.Booking> userBookings = bookingService.getBookingsByUserId(user.getId());

            // Get active restaurants
            List<Restaurant> restaurants = restaurantService.getActiveRestaurants();

            // Calculate stats
            int totalBookings = userBookings.size();
            String favoriteCuisine = calculateFavoriteCuisine(userBookings);

            // Create response map
            Map<String, Object> response = new HashMap<>();
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("email", user.getEmail());
            response.put("totalBookings", totalBookings);
            response.put("favoriteCuisine", favoriteCuisine);
            response.put("bookings", userBookings.stream().map(this::convertBookingToMap).toList());
            response.put("restaurants", restaurants);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\": \"Internal server error\"}");
        }
    }
    @GetMapping("/api/dashboard/restaurant/today-bookings")
@ResponseBody
public ResponseEntity<?> getTodayBookings() {
    try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String email = auth.getName();

        // Fetch owner user
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(401).body("User not found");
        }

        User owner = userOpt.get();

        if (owner.getRestaurantId() == null) {
            return ResponseEntity.ok(List.of());
        }

        String restaurantId = owner.getRestaurantId();

        // ALL bookings for this restaurant
        List<Booking> allBookings = bookingService.getBookingsByRestaurantId(restaurantId);

        // Today
        LocalDate today = LocalDate.now();

        List<Booking> todaysBookings = allBookings.stream()
                .filter(b -> b.getStartTime().toLocalDate().isEqual(today))
                .collect(Collectors.toList());

        return ResponseEntity.ok(todaysBookings);

    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}


    private String calculateFavoriteCuisine(List<com.example.table_booking.model.Booking> bookings) {
        if (bookings.isEmpty()) return "Not yet determined";

        Map<String, Long> cuisineCount = bookings.stream()
            .filter(booking -> booking.getRestaurantId() != null)
            .map(booking -> restaurantService.getRestaurantById(booking.getRestaurantId()).orElse(null))
            .filter(restaurant -> restaurant != null && restaurant.getCuisineType() != null)
            .collect(Collectors.groupingBy(Restaurant::getCuisineType, Collectors.counting()));

        return cuisineCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Not yet determined");
    }

    private Map<String, Object> convertBookingToMap(com.example.table_booking.model.Booking booking) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", booking.getId());
        map.put("restaurantName", restaurantService.getRestaurantById(booking.getRestaurantId())
            .map(Restaurant::getName).orElse("Unknown Restaurant"));
        map.put("date", booking.getStartTime() != null ? booking.getStartTime().toLocalDate().toString() : "");
        map.put("time", booking.getStartTime() != null ? booking.getStartTime().toLocalTime().toString() : "");
        map.put("status", booking.getStatus() != null ? booking.getStatus().toString() : "");
        return map;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    // Inner class for admin stats
    public static class AdminStats {
        private long totalUsers;
        private long activeRestaurants;
        private long pendingApprovals;
        private long totalBookings;

        public AdminStats(long totalUsers, long activeRestaurants, long pendingApprovals, long totalBookings) {
            this.totalUsers = totalUsers;
            this.activeRestaurants = activeRestaurants;
            this.pendingApprovals = pendingApprovals;
            this.totalBookings = totalBookings;
        }

        public long getTotalUsers() { return totalUsers; }
        public long getActiveRestaurants() { return activeRestaurants; }
        public long getPendingApprovals() { return pendingApprovals; }
        public long getTotalBookings() { return totalBookings; }
    }

    // Inner class for restaurant stats
    public static class RestaurantStats {
        private long totalBookings;
        private long availableTables;
        private double averageRating;
        private long totalReviews;

        public RestaurantStats(long totalBookings, long availableTables, double averageRating, long totalReviews) {
            this.totalBookings = totalBookings;
            this.availableTables = availableTables;
            this.averageRating = averageRating;
            this.totalReviews = totalReviews;
        }

        public long getTotalBookings() { return totalBookings; }
        public long getAvailableTables() { return availableTables; }
        public double getAverageRating() { return averageRating; }
        public long getTotalReviews() { return totalReviews; }
    }
}
