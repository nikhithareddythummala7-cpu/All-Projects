package com.example.medlypharma.controller;

import com.example.medlypharma.dto.CartResponseDTO;
import com.example.medlypharma.model.Medicine;
import com.example.medlypharma.model.Order;
import com.example.medlypharma.model.User;
import com.example.medlypharma.service.CartService;
import com.example.medlypharma.service.MedicineService;
import com.example.medlypharma.service.OrderService;
import com.example.medlypharma.service.PrescriptionService;
import com.example.medlypharma.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ViewController {

    private final UserService userService;
    private final OrderService orderService;
    private final PrescriptionService prescriptionService;
    private final MedicineService medicineService;
    private final CartService cartService;

    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout
    ) {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }

    @GetMapping("/medicines")
    public String findMedicines(Model model) {
        // Get current user for display
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            model.addAttribute("fullName", user.getFullName());
        }

        // Add medicines and categories to the model
        List<Medicine> medicines = medicineService.getAllMedicines();
        List<String> categories = medicineService.findAllCategories();

        model.addAttribute("medicines", medicines);
        model.addAttribute("categories", categories);

        return "find-medicines";
    }

    @GetMapping("/prescriptions/upload")
    public String uploadPrescription(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            model.addAttribute("fullName", user.getFullName());
        }
        return "upload-prescription";
    }

    @GetMapping("/orders")
    public String orderHistory(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            try {
                User user = userService.getUserByEmail(email);
                model.addAttribute("fullName", user.getFullName());

                List<Order> orders = orderService.getOrdersByUserEmail(email);
                model.addAttribute("orders", orders);
            } catch (Exception e) {
                log.error("Error loading orders for user {}: {}", email, e.getMessage(), e);
                model.addAttribute("error", "Unable to load order history. Please try again later.");
                model.addAttribute("orders", List.of());
            }
        }
        return "order-history";
    }

    @GetMapping("/profile")
    public String updateProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
            model.addAttribute("fullName", user.getFullName());
        }
        return "update-profile";
    }




}
