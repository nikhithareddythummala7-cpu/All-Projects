package com.example.table_booking.controller;

import com.example.table_booking.model.Restaurant;
import com.example.table_booking.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {

    @Autowired
    private com.example.table_booking.service.AdminService adminService;

    @GetMapping
    public String getAllRestaurants(Model model) {
        model.addAttribute("restaurants", adminService.getAllRestaurantsAdmin());
        return "admin/restaurants";
    }

    @PostMapping("/{id}/approve")
    public String approveRestaurant(@PathVariable("id") String id) {
        adminService.approveRestaurant(id);
        return "redirect:/admin/restaurants";
    }

    @PostMapping("/{id}/reject")
    public String rejectRestaurant(@PathVariable("id") String id) {
        adminService.rejectRestaurant(id);
        return "redirect:/admin/restaurants";
    }
}
