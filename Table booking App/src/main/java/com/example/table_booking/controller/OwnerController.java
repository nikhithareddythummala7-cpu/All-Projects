package com.example.table_booking.controller;

import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.User;
import com.example.table_booking.service.RestaurantService;
import com.example.table_booking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class OwnerController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/owner/bookings")
    public String viewBookings(Model model, Principal principal) {
        String email = principal.getName();
        Optional<Restaurant> restaurantOpt = restaurantService.findRestaurantByOwner(email);
        if (restaurantOpt.isEmpty()) {
            return "redirect:/restaurant/add";
        }
        Restaurant restaurant = restaurantOpt.get();
        model.addAttribute("restaurant", restaurant);
        return "owner/bookings";
    }
    @GetMapping("/owner/tables")
public String viewTables(Model model, Principal principal) {
    String email = principal.getName();
    Optional<Restaurant> restaurantOpt = restaurantService.findRestaurantByOwner(email);

    if (restaurantOpt.isEmpty()) {
        return "redirect:/restaurant/add";
    }

    model.addAttribute("restaurant", restaurantOpt.get());
    return "owner/tables"; // MUST match folder owner/tables.html
}

    @GetMapping("/owner/profile")
    public String ownerProfile(Model model, Principal principal) {
        String email = principal.getName();
        Restaurant restaurant = restaurantService.getRestaurantByOwnerEmail(email);
        model.addAttribute("restaurant", restaurant);
        return "owner/profile";
    }

}
