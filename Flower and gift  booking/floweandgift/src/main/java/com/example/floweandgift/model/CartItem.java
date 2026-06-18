package com.example.floweandgift.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    private String id;
    private String userId; // Reference to User
    private String medicineId; // Reference to Medicine/Product
    private String medicineName;
    private String imageUrl;
    private int quantity;
    private double price; // Unit price at time of adding to cart
}
