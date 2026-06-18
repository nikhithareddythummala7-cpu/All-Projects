package com.example.online_voting.repository;

import com.example.online_voting.model.Election;
import com.example.online_voting.model.ElectionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElectionRepository extends MongoRepository<Election, String> {
    List<Election> findByStatus(ElectionStatus status);
    List<Election> findByStatusOrderByStartDateDesc(ElectionStatus status);
    List<Election> findByCreatedBy(String createdBy);
    List<Election> findByStartDateAfterAndStatusOrderByStartDateAsc(LocalDateTime date, ElectionStatus status);
}
