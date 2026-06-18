package com.example.medlypharma.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cart_items")
public class CartItem {
    @Id
    private String id;
    private String userId;
    private String medicineId;
    private String medicineName;
    private String imageUrl;
    private int quantity;
    private double price;
}