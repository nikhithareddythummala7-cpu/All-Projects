package com.example.medlypharma.service;

import com.example.medlypharma.model.Medicine;
import com.example.medlypharma.model.OrderItem;
import com.example.medlypharma.repository.MedicineRepository;
import com.example.medlypharma.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Medicine createMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @Override
    public Page<Medicine> findAll(Pageable pageable) {
        return medicineRepository.findAll(pageable);
    }

    @Override
    public Optional<Medicine> findById(String id) {
        return medicineRepository.findById(id);
    }

    @Override
    public List<Medicine> findAllMedicines() {
        try {
            List<Medicine> medicines = medicineRepository.findAll();
            return medicines != null ? medicines : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error in findAllMedicines: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // searchMedicines(String) removed to match the MedicineService interface which exposes
    // searchMedicines(String, Pageable) and searchMedicinesByName(String).
    
    @Override
    public List<Medicine> searchMedicinesByName(String name) {
        try {
            return medicineRepository.findByNameContainingIgnoreCase(name, Pageable.unpaged()).getContent();
        } catch (Exception e) {
            System.err.println("Error in searchMedicinesByName: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<String> findAllCategories() {
        try {
            return medicineRepository.findAll().stream()
                .map(Medicine::getCategory)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in findAllCategories: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> findAllManufacturers() {
        try {
            return medicineRepository.findAll().stream()
                .map(Medicine::getManufacturer)
                .filter(manufacturer -> manufacturer != null && !manufacturer.trim().isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in findAllManufacturers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Medicine> filterMedicines(String category, String manufacturer, Double minPrice, Double maxPrice) {
        try {
            return medicineRepository.findAll().stream()
                .filter(medicine -> category == null || category.trim().isEmpty() || category.equals(medicine.getCategory()))
                .filter(medicine -> manufacturer == null || manufacturer.trim().isEmpty() || manufacturer.equals(medicine.getManufacturer()))
                .filter(medicine -> minPrice == null || medicine.getPrice() >= minPrice)
                .filter(medicine -> maxPrice == null || medicine.getPrice() <= maxPrice)
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in filterMedicines: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Medicine> getAllMedicines() {
        try {
            System.out.println("MedicineServiceImpl: Getting all medicines from repository");
            List<Medicine> medicines = medicineRepository.findAll();
            System.out.println("MedicineServiceImpl: Found " + medicines.size() + " medicines in database");
            return medicines;
        } catch (Exception e) {
            System.err.println("MedicineServiceImpl: Error getting medicines: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Page<Medicine> searchMedicines(String name, Pageable pageable) {
        return medicineRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public List<Medicine> findByCategory(String category) {
        return medicineRepository.findByCategory(category);
    }

    @Override
    public Medicine updateMedicine(String id, Medicine medicine) {
        Medicine existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found"));
        existingMedicine.setName(medicine.getName());
        existingMedicine.setDescription(medicine.getDescription());
        existingMedicine.setManufacturer(medicine.getManufacturer());
        existingMedicine.setCategory(medicine.getCategory());
        existingMedicine.setPrice(medicine.getPrice());
        existingMedicine.setQuantityInStock(medicine.getQuantityInStock());
        existingMedicine.setMinimumStockLevel(medicine.getMinimumStockLevel());
        existingMedicine.setDosage(medicine.getDosage());
        existingMedicine.setExpiryDate(medicine.getExpiryDate());
        existingMedicine.setImageUrl(medicine.getImageUrl());
        return medicineRepository.save(existingMedicine);
    }

    @Override
    public void deleteMedicine(String id) {
        medicineRepository.deleteById(id);
    }

    @Override
    public boolean isLowStock(String id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found"));
        return medicine.isLowStock();
    }

    @Override
    public List<Medicine> findLowStockMedicines() {
        return medicineRepository.findByQuantityInStockLessThanEqual(10); // Assuming default minimum stock level
    }

    @Override
    public List<Medicine> findLowStockMedicines(int count) {
        try {
            List<Medicine> low = medicineRepository.findByQuantityInStockLessThanEqual(10);
            if (low == null || low.isEmpty()) return new ArrayList<>();
            return low.size() <= count ? low : low.subList(0, Math.max(0, Math.min(count, low.size())));
        } catch (Exception e) {
            System.err.println("Error in findLowStockMedicines(count): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Medicine> findExpiredMedicines() {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return medicineRepository.findExpiredMedicines(currentDate);
    }

    @Override
    public Medicine updateStock(String id, int quantity) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found"));
        medicine.setQuantityInStock(medicine.getQuantityInStock() + quantity);
        return medicineRepository.save(medicine);
    }

    @Override
    public Medicine getMedicineById(String id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Medicine not found"));
    }

    @Override
    public List<Medicine> getMedicinesByCategory(String category) {
        return findByCategory(category);
    }

    @Override
    public Medicine saveMedicine(Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    @Override
    public long countAllMedicines() {
        return medicineRepository.count();
    }

    @Override
    public List<Medicine> getTopSellingProducts(int limit) {
        try {
            // Get all order items and aggregate sales by medicine
            List<OrderItem> allOrderItems = orderItemRepository.findAll();
            Map<String, Integer> salesByMedicine = new HashMap<>();

            for (OrderItem item : allOrderItems) {
                String medicineId = item.getMedicineId();
                if (medicineId != null) {
                    salesByMedicine.put(medicineId, salesByMedicine.getOrDefault(medicineId, 0) + item.getQuantity());
                }
            }

            // Sort by quantity sold descending and get top products
            return salesByMedicine.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(limit)
                    .map(entry -> {
                        try {
                            Medicine medicine = medicineRepository.findById(entry.getKey()).orElse(null);
                            if (medicine != null) {
                                // Add quantity sold as a transient field (we'll use it in the template)
                                medicine.setQuantityInStock(entry.getValue()); // Temporarily store quantity sold
                                return medicine;
                            }
                        } catch (Exception e) {
                            System.err.println("Error finding medicine: " + e.getMessage());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error in getTopSellingProducts: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
