package com.example.medlypharma.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "medicines")
public class Medicine {
    @Id
    private String id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Indexed(unique = true)
    private String name;
    
    @NotBlank(message = "Description is required")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @NotBlank(message = "Manufacturer is required")
    @Size(max = 100, message = "Manufacturer name cannot exceed 100 characters")
    private String manufacturer;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantityInStock;
    
    @NotNull(message = "Minimum stock level is required")
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    private Integer minimumStockLevel = 10;
    
    @NotBlank(message = "Dosage information is required")
    private String dosage;
    
    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
    
    @NotBlank(message = "Image URL is required")
    @Pattern(regexp = "^(https?|ftp)://.*\\.(jpg|jpeg|png|gif|webp)$", 
             message = "Please provide a valid image URL (jpg, jpeg, png, gif, or webp)")
    private String imageUrl;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @AssertTrue(message = "Quantity in stock must be greater than or equal to minimum stock level")
    public boolean isStockValid() {
        return quantityInStock >= 0 && quantityInStock >= minimumStockLevel;
    }
    
    public boolean isLowStock() {
        return quantityInStock <= minimumStockLevel;
    }
}
