package com.example.medlypharma.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private String fullName;
    private String addressLine1;
    private String city;
    private String state;
    private String pincode;
    private String phone;
    private String paymentMethod;
}
