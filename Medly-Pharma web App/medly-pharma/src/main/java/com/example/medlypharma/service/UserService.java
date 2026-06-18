package com.example.medlypharma.service;

import com.example.medlypharma.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    User createUser(User user);
    User registerUser(com.example.medlypharma.dto.RegisterRequest registerRequest);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findAllUsers();
    User getUserById(String id);
    User getUserByEmail(String email);
    List<User> getAllUsers();
    User updateUser(String id, User user);
    void deleteUser(String id);
    boolean existsByEmail(String email);
    User activateUser(String id);
    User deactivateUser(String id);
    long countAllUsers();
    User authenticateUser(String email, String password);
}
