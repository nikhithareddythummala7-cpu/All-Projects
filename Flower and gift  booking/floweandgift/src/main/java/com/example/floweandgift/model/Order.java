package com.example.floweandgift.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;
    private String userId;
    private List<OrderItem> items;
    private Address deliveryAddress;
    private LocalDateTime deliveryDateTime;
    private String specialInstructions;
    private OrderStatus status;
    private Payment payment;
    private String couponCode;
    private double discountAmount;

    private String deliveryDayOption;
    private String deliverySlot;
    private double deliveryFee;

    private double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private double price;
    }

    public enum OrderStatus {
        PLACED, PACKED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    }

    public enum DeliveryTimeSlot {
        MORNING_9_12, AFTERNOON_12_3, EVENING_3_6, NIGHT_6_9
    }
}
