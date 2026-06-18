package com.groceryapp.freshcart.controller;

import com.groceryapp.freshcart.model.Order;
import com.groceryapp.freshcart.model.Role;
import com.groceryapp.freshcart.model.User;
import com.groceryapp.freshcart.repository.UserRepository;
import com.groceryapp.freshcart.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    // --- USERS ---
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.USER)
                .toList();
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        Optional<User> u = userRepository.findById(userId);
        return u.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getUserOrders(@PathVariable String userId) {
        User u = new User();
        u.setId(userId);
        return orderService.getUserOrders(u);
    }
}
