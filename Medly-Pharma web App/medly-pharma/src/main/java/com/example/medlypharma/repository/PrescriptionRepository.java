package com.example.medlypharma.repository;

import com.example.medlypharma.model.Prescription;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends BaseRepository<Prescription, String> {
    List<Prescription> findByUserId(String userId);
    List<Prescription> findByUserEmail(String userEmail);
    List<Prescription> findByVerified(boolean verified);
}
