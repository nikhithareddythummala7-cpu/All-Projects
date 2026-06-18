package com.example.medlypharma.service;

import com.example.medlypharma.model.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MedicineService {
    Medicine createMedicine(Medicine medicine);
    Page<Medicine> findAll(org.springframework.data.domain.Pageable pageable);
    Optional<Medicine> findById(String id);
    List<Medicine> findAllMedicines();
    List<Medicine> getAllMedicines();
    long countAllMedicines();
    Medicine getMedicineById(String id);
    List<Medicine> getMedicinesByCategory(String category);
    Medicine saveMedicine(Medicine medicine);
    List<Medicine> searchMedicinesByName(String name);
    Page<Medicine> searchMedicines(String name, Pageable pageable);
    List<Medicine> findByCategory(String category);
    List<String> findAllCategories();
    List<String> findAllManufacturers();
    List<Medicine> filterMedicines(String category, String manufacturer, Double minPrice, Double maxPrice);
    List<Medicine> findLowStockMedicines(int count);
    Medicine updateMedicine(String id, Medicine medicine);
    void deleteMedicine(String id);
    boolean isLowStock(String id);
    List<Medicine> findLowStockMedicines();
    List<Medicine> findExpiredMedicines();
    Medicine updateStock(String id, int quantity);
    List<Medicine> getTopSellingProducts(int limit);
}
