package com.streamy.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movies")
public class Movie {
    @Id
    private String id;
    @Indexed(unique = true)
    private String title;
    private String description;
    private List<String> genre;
    private int releaseYear;
    private String thumbnailUrl;
    private String videoUrl;
    private double rating;
    private int likes;
    private boolean isPremium;
    private Instant createdAt;
}
