package com.example.floweandgift.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String id;
    private String name;
    private String slug;
    private String description;
    private double price;
    private int stock;
    private ProductType type;
    private String categoryId;
    private List<String> images;
    private String occasion;
    private String color;
    private boolean fresh;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ProductType {
        FLOWER, GIFT, COMBO, MEDICINE
    }
}
