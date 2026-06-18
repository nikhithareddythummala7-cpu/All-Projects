package com.groceryapp.freshcart.controller;

import com.groceryapp.freshcart.model.CartItem;
import com.groceryapp.freshcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // ----------------- GET USER CART -----------------
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCartItems(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(cartService.getUserCart(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching cart items: " + e.getMessage());
        }
    }

    // ----------------- ADD TO CART -----------------
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addCartItem(@RequestBody CartItemRequest request) {
        try {
            return ResponseEntity.ok(
                    cartService.addToCart(request.getUserId(), request.getProductId(), request.getQuantity())
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding to cart: " + e.getMessage());
        }
    }

    // ----------------- UPDATE CART ITEM -----------------
    @PutMapping("/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateCartItemQuantity(@PathVariable String itemId, @RequestParam Integer quantity) {
        try {
            CartItem updatedItem = cartService.updateCartItemQuantity(itemId, quantity);
            if (updatedItem == null) {
                return ResponseEntity.ok("Item removed from cart successfully");
            }
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating cart item: " + e.getMessage());
        }
    }

    // ----------------- REMOVE CART ITEM -----------------
    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeCartItem(@PathVariable String itemId) {
        try {
            cartService.removeCartItem(itemId);
            return ResponseEntity.ok("Item removed from cart successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing cart item: " + e.getMessage());
        }
    }

    // ----------------- CLEAR USER CART -----------------
    @DeleteMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> clearCart(@PathVariable String userId) {
        try {
            cartService.clearUserCart(userId);
            return ResponseEntity.ok("Cart cleared successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error clearing cart: " + e.getMessage());
        }
    }

    // ----------------- GET CART TOTAL -----------------
    @GetMapping("/total/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCartTotal(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(cartService.calculateCartTotal(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error calculating cart total: " + e.getMessage());
        }
    }

    // ----------------- GET CART ITEM COUNT -----------------
    @GetMapping("/count/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCartItemCount(@PathVariable String userId) {
        try {
            return ResponseEntity.ok(cartService.getCartItemCount(userId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting cart item count: " + e.getMessage());
        }
    }

    // ----------------- REQUEST DTO -----------------
    public static class CartItemRequest {
        private String userId;     // changed Long → String
        private String productId;  // changed Long → String
        private Integer quantity;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
