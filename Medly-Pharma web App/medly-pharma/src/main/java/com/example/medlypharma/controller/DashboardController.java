package com.example.medlypharma.controller;

import com.example.medlypharma.model.Order;
import com.example.medlypharma.model.Prescription;
import com.example.medlypharma.model.User;
import com.example.medlypharma.service.OrderService;
import com.example.medlypharma.service.PrescriptionService;
import com.example.medlypharma.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final UserService userService;
    private final OrderService orderService;
    private final PrescriptionService prescriptionService;

    public DashboardController(UserService userService, OrderService orderService, PrescriptionService prescriptionService) {
        this.userService = userService;
        this.orderService = orderService;
        this.prescriptionService = prescriptionService;
    }

    @GetMapping({"/", "/dashboard", "/user/dashboard"})
    public String dashboard(Model model) {
        // Check user role and redirect accordingly
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }
        
        // For regular users, show the user dashboard
        // Get the current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        try {
            // Fetch user details
            User user = userService.getUserByEmail(email);
            model.addAttribute("fullName", user.getFullName());
            model.addAttribute("username", email);

            // Active Prescriptions count (verified prescriptions)
            long activePrescriptions = prescriptionService.getPrescriptionsByUserEmail(email).stream()
                    .filter(Prescription::isVerified)
                    .count();
            model.addAttribute("activePrescriptions", activePrescriptions);

            // Recent Orders count (last 30 days)
            List<Order> recentOrders = orderService.getOrdersByUserEmail(email).stream()
                    .filter(order -> order.getOrderDate().isAfter(LocalDateTime.now().minusDays(30)))
                    .collect(Collectors.toList());
            model.addAttribute("recentOrdersCount", recentOrders.size());

            // Next Refill (placeholder - N/A as no refill date field in models)
            model.addAttribute("nextRefill", "N/A");

            // Recent Activity (combine recent orders and prescriptions)
            List<String> activities = new ArrayList<>();
            recentOrders.forEach(order -> activities.add("Ordered on " + order.getOrderDate().toLocalDate()));
            prescriptionService.getPrescriptionsByUserEmail(email).stream()
                    .sorted((p1, p2) -> p2.getUploadDate().compareTo(p1.getUploadDate()))
                    .limit(5)
                    .forEach(p -> activities.add("Uploaded prescription on " + p.getUploadDate().toLocalDate()));
            model.addAttribute("activities", activities);
        } catch (Exception e) {
            // Handle errors gracefully
            // Log the error for debugging
            System.err.println("Error in dashboard: " + e.getMessage());
            e.printStackTrace();
            
            // Set default values for the view
            model.addAttribute("error", "Unable to load dashboard data. Please try again later.");
            model.addAttribute("fullName", "User");
            model.addAttribute("username", email);
            model.addAttribute("activePrescriptions", 0);
            model.addAttribute("recentOrdersCount", 0);
            model.addAttribute("nextRefill", "N/A");
            model.addAttribute("activities", List.of());
        }

        // Check if the user has admin role
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return "redirect:/admin/dashboard";
        }

        return "dashboard";
    }
}
