package com.example.medlypharma.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class PrescriptionDTO {
    private String id;

    @NotBlank(message = "File name is required")
    private String fileName;

    private String filePath;
    private String fileType;
    private long fileSize;
    private LocalDateTime uploadDate;
    private boolean verified;
    private String userId;
    private String userFullName;
    private String patientName;
    private String doctorName;
    private String downloadUrl;
}
