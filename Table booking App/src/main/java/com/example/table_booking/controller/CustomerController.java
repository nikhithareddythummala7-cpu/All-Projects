package com.example.table_booking.controller;

import com.example.table_booking.model.Booking;
import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.Table;
import com.example.table_booking.model.User;
import com.example.table_booking.service.RestaurantService;
import com.example.table_booking.service.TableService;
import com.example.table_booking.service.UserService;
import com.example.table_booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("")
public class CustomerController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private TableService tableService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/restaurants/list")
    public String listRestaurants(Model model) {
        List<Restaurant> restaurants = restaurantService.getActiveRestaurants();
        model.addAttribute("restaurants", restaurants);
        return "restaurantList";
    }

    @GetMapping("/restaurants/{id}")
    public String restaurantDetails(@PathVariable String id, Model model) {
        Optional<Restaurant> restaurant = restaurantService.getRestaurantById(id);
        if (restaurant.isPresent()) {
            List<Table> tables = tableService.getTablesByRestaurantId(id);
            model.addAttribute("restaurant", restaurant.get());
            model.addAttribute("tables", tables);
            return "restaurantDetails";
        }
        return "redirect:/restaurants/list";
    }

    @GetMapping("/restaurants/{id}/book")
    public String bookTable(@PathVariable String id, Model model) {
        Optional<Restaurant> restaurant = restaurantService.getRestaurantById(id);
        if (restaurant.isPresent()) {
            Booking booking = new Booking();
            booking.setRestaurantId(id);
            model.addAttribute("booking", booking);
            model.addAttribute("selectedRestaurant", restaurant.get());
            model.addAttribute("restaurants", restaurantService.getActiveRestaurants());
            return "createBooking";
        }
        return "redirect:/restaurants/list";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            // Show login prompt for unauthenticated users
            model.addAttribute("errorMessage", "Please log in to view your profile.");
            return "restaurant/profile";
        }

        String username = auth.getName();
        Optional<User> userOpt = userService.getUserByEmail(username);
        if (!userOpt.isPresent()) {
            model.addAttribute("errorMessage", "User not found.");
            return "restaurant/profile";
        }

        User user = userOpt.get();
        if (user.getRole() == User.UserRole.RESTAURANT_OWNER) {
            // Redirect to restaurant profile
            return "redirect:/restaurant/profile";
        } else if (user.getRole() == User.UserRole.CUSTOMER) {
            // Redirect to customer dashboard
            return "redirect:/customer/dashboard";
        } else {
            // Admin
            return "redirect:/admin/dashboard";
        }
    }

    @GetMapping("/customer/profile")
    public String customerProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            // If not authenticated, redirect to login
            return "redirect:/login";
        }

        String username = auth.getName();
        Optional<User> userOpt = userService.getUserByEmail(username);

        if (!userOpt.isPresent()) {
            // Redirect or show error page; here redirecting to login
            return "redirect:/login";
        }

        User user = userOpt.get();

        if (user.getRole() != User.UserRole.CUSTOMER) {
            // Prevent access if user is not a customer
            return "redirect:/";
        }

        model.addAttribute("user", user);

        return "customer/profile";
    }
}
