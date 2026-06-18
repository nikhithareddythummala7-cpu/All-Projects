package com.SmartBridge.Task_Management.repository;

import com.SmartBridge.Task_Management.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}
