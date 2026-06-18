package com.example.floweandgift.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "wishlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {
    @Id
    private String id;
    private String userId;
    private List<String> productIds; // List of product IDs
}
