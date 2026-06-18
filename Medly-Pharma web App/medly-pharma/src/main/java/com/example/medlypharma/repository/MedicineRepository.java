package com.example.medlypharma.repository;

import com.example.medlypharma.model.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends BaseRepository<Medicine, String> {
    List<Medicine> findByCategory(String category);
    Page<Medicine> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Medicine> findByQuantityInStockLessThanEqual(int quantity);
    
    @Query("{'expiryDate': {$lt: ?0}}")
    List<Medicine> findExpiredMedicines(String currentDate);
}
