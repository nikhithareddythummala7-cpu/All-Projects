package com.example.online_voting.config;

import com.example.online_voting.model.User;
import com.example.online_voting.model.UserRole;
import com.example.online_voting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    @Autowired
    private UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seedAdmin() {
        try {
            String adminEmail = "admin@voting.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setFirstName("System");
                admin.setLastName("Admin");
                admin.setVoterId("ADMIN001"); // Set voterId to avoid null unique index constraint
                admin.setPassword(new BCryptPasswordEncoder().encode("admin123"));
                admin.setRole(UserRole.ADMIN);
                admin.setApproved(true);
                admin.setActive(true);
                User savedAdmin = userRepository.save(admin);
                System.out.println("✓ Admin user seeded successfully: " + savedAdmin.getEmail());
            } else {
                System.out.println("✓ Admin user already exists: " + adminEmail);
            }
        } catch (Exception e) {
            System.err.println("✗ Error seeding admin user: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
