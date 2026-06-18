package com.example.floweandgift.service;

import com.example.floweandgift.model.User;
import com.example.floweandgift.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        System.out.println("=== UserService.registerUser() called ===");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            System.out.println("Username already exists: " + user.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            System.out.println("Email already exists: " + user.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Set password (encrypted)
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        System.out.println("Password encrypted");
        
        // Set role
        user.setRole(user.getRole() != null ? user.getRole() : User.Role.USER);
        System.out.println("Role set: " + user.getRole());
        
        // Set active status
        user.setActive(true);
        
        // Set timestamps
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        System.out.println("Timestamps set");
        
        // Save to database
        User savedUser = userRepository.save(user);
        System.out.println("User saved to database with ID: " + savedUser.getId());
        System.out.println("=== UserService.registerUser() completed successfully ===");
        
        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
