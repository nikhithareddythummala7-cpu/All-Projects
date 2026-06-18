package com.example.online_voting.service;

import com.example.online_voting.model.User;
import com.example.online_voting.model.UserRole;
import com.example.online_voting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerVoter(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        
        if (user.getVoterId() != null && userRepository.findByVoterId(user.getVoterId()).isPresent()) {
            throw new RuntimeException("Voter ID already registered");
        }
        
        user.setRole(UserRole.VOTER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRegistrationDate(LocalDateTime.now());
        user.setApproved(true);  // Auto-approve for demo purposes
        user.setActive(true);
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }
    
    public List<User> getPendingVoters() {
        return userRepository.findByApprovedAndRole(false, UserRole.VOTER);
    }
    
    public User approveVoter(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setApproved(true);
        return userRepository.save(user);
    }
    
    public User rejectVoter(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(false);
        return userRepository.save(user);
    }
    
    public User updateLastLogin(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setLastLoginDate(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public User changePassword(String userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
    
    public List<User> getAllElectionOfficers() {
        return userRepository.findByRole(UserRole.ELECTION_OFFICER);
    }

    public User createElectionOfficer(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        user.setRole(UserRole.ELECTION_OFFICER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRegistrationDate(LocalDateTime.now());
        user.setApproved(true);
        user.setActive(false); // created inactive by default; admin can activate
        // Generate unique voterId for officer if not set
        if (user.getVoterId() == null || user.getVoterId().isEmpty()) {
            user.setVoterId("OFFICER_" + System.currentTimeMillis());
        }
        return userRepository.save(user);
    }

    public User activateOfficer(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Officer not found"));
        if (user.getRole() != UserRole.ELECTION_OFFICER) {
            throw new RuntimeException("User is not an election officer");
        }
        user.setActive(true);
        return userRepository.save(user);
    }

    public User deactivateOfficer(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Officer not found"));
        if (user.getRole() != UserRole.ELECTION_OFFICER) {
            throw new RuntimeException("User is not an election officer");
        }
        user.setActive(false);
        return userRepository.save(user);
    }
    
    public List<User> getAllAdmins() {
        return userRepository.findByRole(UserRole.ADMIN);
    }

    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
}
