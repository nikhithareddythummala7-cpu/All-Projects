package com.example.floweandgift.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "prescriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    private String id;
    private String fileName;
    private String filePath;
    private String fileType; // e.g., "PDF", "JPG", "PNG"
    private String contentType; // e.g., "application/pdf", "image/jpeg"
    private long fileSize; // in bytes
    private LocalDateTime uploadDate;
    private boolean verified; // Flag for admin verification
    private String userEmail;
    private String patientName;
    private String doctorName;
    private String userId; // Reference to User
    private LocalDateTime updatedAt;
}
