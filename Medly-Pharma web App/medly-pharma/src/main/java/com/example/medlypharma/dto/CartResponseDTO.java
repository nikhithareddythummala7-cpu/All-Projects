package com.example.medlypharma.dto;

import com.example.medlypharma.model.CartItem;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class CartResponseDTO {
    // full cart payload
    private List<CartItem> items;
    private double totalAmount;
    private double deliveryCost;
    private int totalItems;

    // Convenience: single-item response (backwards compatible)
    private CartItem cartItem;

    public CartResponseDTO() {
    }

    // Backwards-compatible constructor used by existing controllers
    public CartResponseDTO(CartItem cartItem, int totalItems) {
        this.cartItem = cartItem;
        this.totalItems = totalItems;
        this.items = cartItem != null ? Collections.singletonList(cartItem) : Collections.emptyList();
        this.totalAmount = cartItem != null ? cartItem.getQuantity() * cartItem.getPrice() : 0.0;
        this.deliveryCost = 0.0;
    }

    // Full constructor for getCart responses
    public CartResponseDTO(List<CartItem> items, double totalAmount, double deliveryCost, int totalItems) {
        this.items = items;
        this.totalAmount = totalAmount;
        this.deliveryCost = deliveryCost;
        this.totalItems = totalItems;
    }
}