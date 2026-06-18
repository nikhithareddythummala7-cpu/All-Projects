package com.example.table_booking.config;

import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.User;
import com.example.table_booking.repository.RestaurantRepository;
import com.example.table_booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed admin user if not exists
        if (userRepository.findByEmail("admin@tablebooking.com").stream().findFirst().isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@tablebooking.com");
            admin.setRole(User.UserRole.ADMIN);
            admin.setAccountVerified(true);
            userRepository.save(admin);
            System.out.println("Admin user seeded: email=admin@tablebooking.com, password=admin123");
        }

        // Seed test customer user
        if (userRepository.findByEmail("test@example.com").stream().findFirst().isEmpty()) {
            User customer = new User();
            customer.setUsername("testuser");
            customer.setPassword(passwordEncoder.encode("password123"));
            customer.setEmail("test@example.com");
            customer.setRole(User.UserRole.CUSTOMER);
            customer.setAccountVerified(true);
            userRepository.save(customer);
            System.out.println("Test customer user seeded: email=test@example.com, password=password123");
        }

        // Seed test restaurant owner user (verified for testing)
        if (userRepository.findByEmail("vamshikri123@gmail.com").stream().findFirst().isEmpty()) {
            User restaurantOwner = new User();
            restaurantOwner.setUsername("vamshi");
            restaurantOwner.setPassword(passwordEncoder.encode("password123"));
            restaurantOwner.setEmail("vamshikri123@gmail.com");
            restaurantOwner.setRole(User.UserRole.RESTAURANT_OWNER);
            restaurantOwner.setAccountVerified(true); // Verified for testing
            userRepository.save(restaurantOwner);

            // Seed a restaurant for the owner
            Restaurant restaurant = new Restaurant();
            restaurant.setName("Vamshi's Restaurant");
            restaurant.setDescription("A test restaurant for the owner");
            restaurant.setAddress("123 Test Street");
            restaurant.setPhoneNumber("123-456-7890");
            restaurant.setOwnerEmail("restaurant@vamshi.com");
            restaurant.setOwnerId(restaurantOwner.getId());
            restaurantRepository.save(restaurant);

            // Associate restaurant with user
            restaurantOwner.setRestaurantId(restaurant.getId());
            userRepository.save(restaurantOwner);

            System.out.println("Test restaurant owner user seeded: email=vamshikri123@gmail.com, password=password123, verified=true");
            System.out.println("Restaurant seeded for owner: " + restaurant.getName());
        }

        // Seed user sri123@gmail.com for testing
        if (userRepository.findByEmail("sri123@gmail.com").stream().findFirst().isEmpty()) {
            User customer = new User();
            customer.setUsername("sri123");
            customer.setPassword(passwordEncoder.encode("password123"));
            customer.setEmail("sri123@gmail.com");
            customer.setRole(User.UserRole.CUSTOMER);
            customer.setAccountVerified(true);
            userRepository.save(customer);
            System.out.println("Test customer user seeded: email=sri123@gmail.com, password=password123");
        }

        // Seed test user shivani123@gmail.com with bcrypt hash
        if (userRepository.findByEmail("shivani123@gmail.com").stream().findFirst().isEmpty()) {
            User testUser = new User();
            testUser.setUsername("shivani123");
            testUser.setPassword("$2a$10$2q5NFQ6AgW8Bz2.s3gC8FOBHQ1OkH9oJf3N7KUB6bqlnJfqG2OsEi"); // bcrypt hash for "123456"
            testUser.setEmail("shivani123@gmail.com");
            testUser.setRole(User.UserRole.RESTAURANT_OWNER);
            testUser.setAccountVerified(true);
            userRepository.save(testUser);
            System.out.println("Test user seeded: email=shivani123@gmail.com, raw password=123456, bcrypt hash=$2a$10$2q5NFQ6AgW8Bz2.s3gC8FOBHQ1OkH9oJf3N7KUB6bqlnJfqG2OsEi");
        }
    }
}
