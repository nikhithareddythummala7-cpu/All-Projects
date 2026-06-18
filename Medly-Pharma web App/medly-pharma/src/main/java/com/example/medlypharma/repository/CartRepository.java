package com.example.medlypharma.repository;

import com.example.medlypharma.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<CartItem, String> {
    List<CartItem> findByUserId(String userId);
    Optional<CartItem> findByUserIdAndMedicineId(String userId, String medicineId);
    List<CartItem> findAllByUserIdAndMedicineId(String userId, String medicineId);
    void deleteByUserIdAndMedicineId(String userId, String medicineId);
    void deleteByUserId(String userId);
    int countByUserId(String userId);
}