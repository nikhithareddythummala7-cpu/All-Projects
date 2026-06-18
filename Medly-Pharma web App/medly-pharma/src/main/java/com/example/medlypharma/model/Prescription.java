package com.example.medlypharma.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "prescriptions")
public class Prescription {
    @Id
    private String id;
    
    private String fileName;
    private String filePath;
    private String fileType;
    private String contentType;
    private long fileSize;
    private LocalDateTime uploadDate = LocalDateTime.now();
    private boolean verified = false;
    private String userEmail;
    private String patientName;
    private String doctorName;

    @DBRef
    private User user;
    
    public String getDownloadUrl() {
        return "/api/prescriptions/" + id + "/download";
    }
}
