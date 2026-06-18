package com.example.medlypharma.repository;

import com.example.medlypharma.model.User;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
