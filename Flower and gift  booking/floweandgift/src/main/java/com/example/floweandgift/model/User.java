package com.example.floweandgift.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    @Indexed(unique = true)
    private String email;
    private String password;
    private String fullName;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String phone;
    private String address;
    private List<Address> addresses; // Multiple delivery addresses
    private Role role;
    private boolean active;
    private boolean accountLocked;
    private List<String> prescriptionIds; // List of prescription IDs
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        USER, ADMIN, CUSTOMER
    }
}
