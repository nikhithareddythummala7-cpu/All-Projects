package com.streamy.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "watch_history")
public class WatchHistory {
    @Id
    private String id;
    private String userId;
    private String movieId;
    private double progress;
    private Instant watchedAt;
}
