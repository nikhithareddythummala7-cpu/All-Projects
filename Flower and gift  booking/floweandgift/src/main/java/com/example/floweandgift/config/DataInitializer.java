package com.example.floweandgift.config;

import com.example.floweandgift.model.User;
import com.example.floweandgift.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        System.out.println("DataInitializer: Checking for admin user...");
        try {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(User.Role.ADMIN);
                admin.setFullName("Admin User");
                admin.setPhoneNumber("1234567890");
                admin.setActive(true);
                admin.setAccountLocked(false);
                admin.setCreatedAt(java.time.LocalDateTime.now());
                admin.setUpdatedAt(java.time.LocalDateTime.now());
                userRepository.save(admin);
                System.out.println("Admin user created with username: admin and password: admin123");
            } else {
                System.out.println("Admin user already exists.");
            }
        } catch (Exception e) {
            System.err.println("Error creating admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
