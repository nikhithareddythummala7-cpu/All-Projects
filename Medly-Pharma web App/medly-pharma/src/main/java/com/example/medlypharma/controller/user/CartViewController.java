package com.example.medlypharma.controller.user;

import com.example.medlypharma.dto.CartResponseDTO;
import com.example.medlypharma.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartViewController {

    private static final double DEFAULT_DELIVERY_COST = 50.0;
    
    private final CartService cartService;

    @GetMapping
    public String viewCart(Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        log.info("Accessing cart page");
        
        // Check authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("User not authenticated, redirecting to login");
            redirectAttributes.addAttribute("redirect", "/cart");
            return "redirect:/login";
        }

        try {
            String userEmail = authentication.getName();
            log.info("Loading cart for user: {}", userEmail);
            
            // Get cart items for the user
            List<Map<String, Object>> cartItems = cartService.getCartItemsByUser(userEmail);
            log.debug("Found {} items in cart for user: {}", cartItems.size(), userEmail);
            
            // Calculate totals
            double subtotal = cartItems.stream()
                .mapToDouble(item -> (Double) item.getOrDefault("total", 0.0))
                .sum();
            
            double totalWithDelivery = subtotal + DEFAULT_DELIVERY_COST;
            
            // Add attributes to model
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("subtotal", subtotal);
            model.addAttribute("deliveryCost", DEFAULT_DELIVERY_COST);
            model.addAttribute("totalWithDelivery", totalWithDelivery);
            model.addAttribute("hasItems", !cartItems.isEmpty());
            
            log.debug("Cart page loaded successfully for user: {}", userEmail);
            return "cart";
            
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "User not found. Please log in again.");
            return "redirect:/login";
            
        } catch (Exception e) {
            log.error("Error loading cart page: {}", e.getMessage(), e);
            model.addAttribute("error", "An error occurred while loading your cart. Please try again later.");
            model.addAttribute("hasItems", false);
            return "cart";
        }
    }
}
