package com.example.floweandgift.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    private String id;
    private String productId;
    private String userId;
    private String username; // For display purposes
    private int rating; // 1-5 stars
    private String comment;
    private LocalDateTime createdAt;
    private boolean approved; // For moderation
}
