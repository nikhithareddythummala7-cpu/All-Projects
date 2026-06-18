package com.example.medlypharma.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private String medicineId;
    private int quantity;
}