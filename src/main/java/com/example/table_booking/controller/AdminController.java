package com.example.table_booking.controller;

import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.User;
import com.example.table_booking.service.BookingService;
import com.example.table_booking.service.RestaurantService;
import com.example.table_booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private BookingService bookingService;

    // API Endpoints for Admins

    @GetMapping("/users")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/restaurants/pending")
    @ResponseBody
    public List<Restaurant> getPendingRestaurants() {
        return restaurantService.getAllRestaurants().stream()
                .filter(r -> !r.isVerified())
                .toList();
    }

    @PutMapping("/restaurants/{id}/verify")
    @ResponseBody
    public ResponseEntity<Void> verifyRestaurant(@PathVariable String id) {
        boolean verified = restaurantService.verifyRestaurant(id);
        if (verified) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/users/{id}/verify")
    @ResponseBody
    public ResponseEntity<Void> verifyUser(@PathVariable String id) {
        boolean verified = userService.verifyUser(id);
        if (verified) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/restaurants/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<?> getSystemStats() {
        long totalUsers = userService.getAllUsers().size();
        long totalRestaurants = restaurantService.getAllRestaurants().size();
        long totalBookings = bookingService.getAllBookings().size();

        return ResponseEntity.ok(new SystemStats(totalUsers, totalRestaurants, totalBookings));
    }

    // Thymeleaf Views for Admins - moved to DashboardController

    // Inner class for stats response
    public static class SystemStats {
        private long totalUsers;
        private long totalRestaurants;
        private long totalBookings;

        public SystemStats(long totalUsers, long totalRestaurants, long totalBookings) {
            this.totalUsers = totalUsers;
            this.totalRestaurants = totalRestaurants;
            this.totalBookings = totalBookings;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getTotalRestaurants() {
            return totalRestaurants;
        }

        public long getTotalBookings() {
            return totalBookings;
        }
    }
}
