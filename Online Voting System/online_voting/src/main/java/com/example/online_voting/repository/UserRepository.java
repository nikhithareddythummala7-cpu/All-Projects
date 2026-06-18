package com.example.online_voting.repository;

import com.example.online_voting.model.User;
import com.example.online_voting.model.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVoterId(String voterId);
    Optional<User> findByAadhaarId(String aadhaarId);
    List<User> findByRole(UserRole role);
    List<User> findByApprovedAndRole(boolean approved, UserRole role);
    List<User> findByActive(boolean active);
}
