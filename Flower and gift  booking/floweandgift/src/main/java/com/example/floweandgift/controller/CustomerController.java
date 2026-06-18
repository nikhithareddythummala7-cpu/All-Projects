package com.example.floweandgift.controller;

import com.example.floweandgift.model.Order;
import com.example.floweandgift.model.User;
import com.example.floweandgift.service.CartService;
import com.example.floweandgift.service.OrderService;
import com.example.floweandgift.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    // 🔥 Redirect customer home → main homepage
    @GetMapping("/home")
    public String home() {
        return "redirect:/";   // redirect to main page
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).get();
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/cart")
    public String cart() {
        return "redirect:/cart";
    }

    @GetMapping("/orders")
    public String orders(Authentication authentication, Model model) {
        try {
            System.out.println("Entering /orders endpoint");
            String username = authentication.getName();
            System.out.println("Authenticated user: " + username);
            
            Optional<User> userOptional = userService.findByUsername(username);
            if (userOptional.isEmpty()) {
                System.out.println("User not found: " + username);
                return "redirect:/login?error=user_not_found";
            }
            
            User user = userOptional.get();
            System.out.println("Found user with ID: " + user.getId());
            
            List<Order> orders = orderService.getOrdersByUser(user.getId());
            System.out.println("Found " + orders.size() + " orders for user " + user.getId());
            
            model.addAttribute("orders", orders != null ? orders : new ArrayList<>());
            return "orders/history";
        } catch (Exception e) {
            System.err.println("Error in /orders endpoint: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while loading your orders. Please try again later.");
            return "error/500";
        }
    }
}
