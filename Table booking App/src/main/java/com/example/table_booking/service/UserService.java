package com.example.table_booking.service;

import com.example.table_booking.model.User;
import com.example.table_booking.repository.UserRepository;
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        List<User> users = userRepository.findByEmail(email.toLowerCase());
        if (users.isEmpty()) {
            return Optional.empty();
        }
        if (users.size() > 1) {
            throw new RuntimeException("Multiple users found with email: " + email);
        }
        return Optional.of(users.get(0));
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) {
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(String id, User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setUsername(user.getUsername());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setFirstName(user.getFirstName());
                    existingUser.setLastName(user.getLastName());
                    existingUser.setPhoneNumber(user.getPhoneNumber());
                    existingUser.setRole(user.getRole());
                    existingUser.setAccountVerified(user.isAccountVerified());
                    existingUser.setRestaurantId(user.getRestaurantId());
                    // Update password if provided
                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    }
                    return userRepository.save(existingUser);
                })
                .orElse(null);
    }

    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }

    public boolean verifyUser(String id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setAccountVerified(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
