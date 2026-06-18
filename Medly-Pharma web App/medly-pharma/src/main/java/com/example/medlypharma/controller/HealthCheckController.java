package com.example.medlypharma.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    private final MongoClient mongoClient;

    public HealthCheckController(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try {
            // Check MongoDB connection
            MongoDatabase db = mongoClient.getDatabase("medly-pharma");
            Document pingCommand = new Document("ping", 1);
            db.runCommand(pingCommand);
            
            return ResponseEntity.ok("Application is healthy");
        } catch (Exception e) {
            return ResponseEntity.status(503)
                .body("MongoDB connection failed: " + e.getMessage());
        }
    }
}
