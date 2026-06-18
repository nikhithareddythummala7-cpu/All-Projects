package com.example.medlypharma;

import com.example.medlypharma.model.User;
import com.example.medlypharma.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableMongoAuditing
public class MedlyPharmaApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(MedlyPharmaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if it doesn't exist
        if (userRepository.findByEmail("admin@medlypharma.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@medlypharma.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setRole("ADMIN");
            admin.setActive(true);
            userRepository.save(admin);
            System.out.println("Admin user created successfully!");
            System.out.println("Email: admin@medlypharma.com");
            System.out.println("Password: admin123");
            
            System.out.println("Role: ADMIN");
        } else {
            System.out.println("Admin user already exists");
        }
    }
}
