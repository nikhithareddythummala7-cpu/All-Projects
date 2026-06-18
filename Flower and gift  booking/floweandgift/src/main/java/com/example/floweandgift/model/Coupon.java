package com.example.floweandgift.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    private String id;
    private String code; // Unique coupon code
    private String description;
    private CouponType type; // PERCENTAGE or FIXED
    private double discountValue; // Percentage (0-100) or fixed amount
    private double minimumCartValue; // Minimum cart value to apply
    private LocalDateTime expiryDate;
    private boolean active;
    private int usageLimit; // Maximum number of uses
    private int usedCount; // Current usage count

    public enum CouponType {
        PERCENTAGE, FIXED
    }

    public boolean isValid() {
        return active && LocalDateTime.now().isBefore(expiryDate) && usedCount < usageLimit;
    }

    public double calculateDiscount(double cartTotal) {
        if (!isValid() || cartTotal < minimumCartValue) {
            return 0.0;
        }
        if (type == CouponType.PERCENTAGE) {
            return cartTotal * (discountValue / 100.0);
        } else {
            return Math.min(discountValue, cartTotal);
        }
    }
}
