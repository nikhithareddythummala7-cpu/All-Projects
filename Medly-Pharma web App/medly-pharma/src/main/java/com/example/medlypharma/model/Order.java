package com.example.medlypharma.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    
    @DBRef
    private User user;

    private String userEmail;
    private String orderNumber;
    private LocalDateTime orderDate = LocalDateTime.now();
    private LocalDateTime createdAt = LocalDateTime.now();
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private OrderStatus status = OrderStatus.ORDERED;
    
    @DBRef
    private Set<OrderItem> items = new HashSet<>();
    
    @DBRef
    private Prescription prescription;
    
    public enum OrderStatus {
        ORDERED, PACKED, SHIPPED, DELIVERED, CANCELLED
    }
}
