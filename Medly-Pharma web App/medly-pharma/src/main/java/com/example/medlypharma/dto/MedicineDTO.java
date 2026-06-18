package com.example.medlypharma.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MedicineDTO {
    private String id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotBlank(message = "Manufacturer is required")
    private String manufacturer;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @PositiveOrZero(message = "Quantity in stock must be non-negative")
    private int quantityInStock;

    private int minimumStockLevel;
    private String dosage;
    private LocalDate expiryDate;
    private String imageUrl;
    private boolean lowStock;
}
