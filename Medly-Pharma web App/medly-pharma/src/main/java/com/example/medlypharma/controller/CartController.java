package com.example.medlypharma.controller;

import com.example.medlypharma.dto.CartItemDTO;
import com.example.medlypharma.dto.CartResponseDTO;
import com.example.medlypharma.model.CartItem;
import com.example.medlypharma.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemDTO cartItemDTO, Authentication authentication) {
        try {
            // Authentication check with detailed error message
            if (authentication == null) {
                log.warn("Authentication object is null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Please log in to add items to cart"));
            }
            if (!authentication.isAuthenticated()) {
                log.warn("User not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Your session has expired. Please log in again."));
            }

            // Request validation with specific error messages
            if (cartItemDTO == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Request body cannot be empty"));
            }
            if (cartItemDTO.getMedicineId() == null || cartItemDTO.getMedicineId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Medicine ID is required"));
            }

            String userEmail = authentication.getName();
            if (userEmail == null || userEmail.trim().isEmpty()) {
                log.error("User email is null or empty in authenticated session");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Invalid user session. Please try logging in again."));
            }

            String medicineId = cartItemDTO.getMedicineId().trim();
            int quantity = Math.max(1, cartItemDTO.getQuantity()); // Ensure minimum quantity of 1
            
            log.debug("Processing addToCart request - user={}, medicineId={}, quantity={}", 
                    userEmail, medicineId, quantity);

            // Add to cart and get updated cart data
            CartResponseDTO cart = cartService.addToCart(userEmail, medicineId, quantity);
            
            // Ensure cart is not null and has expected data
            if (cart == null) {
                log.error("Cart service returned null for user={}", userEmail);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update cart. Please try again."));
            }

            // Log success with defensive null checks
            String itemsStr = cart.getItems() != null ? 
                cart.getItems().stream()
                    .map(i -> i.getMedicineName() + "(" + i.getQuantity() + ")")
                    .toList()
                    .toString() : 
                "[]";

            log.info("Successfully updated cart - user={}, medicineId={}, totalItems={}", 
                    userEmail, medicineId, cart.getTotalItems());
            log.info("Cart details - items={}, totalAmount={}", itemsStr, cart.getTotalAmount());

            return ResponseEntity.ok(cart);

        } catch (IllegalArgumentException e) {
            // Handle validation errors (invalid IDs, quantities, etc)
            log.warn("Validation error adding item to cart: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            // Handle state errors (out of stock, etc)
            log.warn("State error adding item to cart: {}", e.getMessage());
            return ResponseEntity.unprocessableEntity()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Handle unexpected errors
            log.error("Unexpected error adding item to cart: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred. Please try again."));
        }
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(cartService.getCart(userEmail));
    }

    @PostMapping("/update")
    public ResponseEntity<CartResponseDTO> updateCartItem(@RequestBody CartItemDTO cartItemDTO, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String userEmail = authentication.getName();
            cartService.updateCartItemQuantity(userEmail, cartItemDTO.getMedicineId(), cartItemDTO.getQuantity());
            // return full cart payload
            CartResponseDTO cart = cartService.getCart(userEmail);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error updating cart item: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartResponseDTO());
        }
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeFromCartPost(@RequestBody CartItemDTO cartItemDTO, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }
            String userEmail = authentication.getName();
            cartService.removeFromCart(userEmail, cartItemDTO.getMedicineId());
            // return updated full cart payload
            CartResponseDTO cart = cartService.getCart(userEmail);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error removing cart item: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove item: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{medicineId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(@PathVariable String medicineId, Authentication authentication) {
        String userEmail = authentication.getName();
        cartService.removeFromCart(userEmail, medicineId);
        int totalItems = cartService.getCartItemCount(userEmail);
        return ResponseEntity.ok(new CartResponseDTO(null, totalItems));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String userEmail = authentication.getName();
        cartService.clearCart(userEmail);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getCartItemCount(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.ok(Collections.singletonMap("count", 0));
            }
            String userEmail = authentication.getName();
            int count = cartService.getCartItemCount(userEmail);
            return ResponseEntity.ok(Collections.singletonMap("count", count));
        } catch (Exception e) {
            log.error("Error getting cart count: {}", e.getMessage(), e);
            return ResponseEntity.ok(Collections.singletonMap("count", 0));
        }
    }
}