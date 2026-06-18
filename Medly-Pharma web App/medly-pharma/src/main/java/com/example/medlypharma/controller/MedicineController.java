package com.example.medlypharma.controller;

import com.example.medlypharma.dto.MedicineDTO;
import com.example.medlypharma.model.Medicine;
import com.example.medlypharma.service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<List<MedicineDTO>> getAllMedicines() {
        System.out.println("MedicineController: Getting all medicines");
        List<Medicine> medicines = medicineService.getAllMedicines();
        System.out.println("MedicineController: Found " + medicines.size() + " medicines");
        List<MedicineDTO> medicineDTOs = medicines.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        System.out.println("MedicineController: Converted to DTOs, returning response");
        return ResponseEntity.ok(medicineDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicineDTO> getMedicineById(@PathVariable String id) {
        Medicine medicine = medicineService.getMedicineById(id);
        MedicineDTO medicineDTO = convertToDTO(medicine);
        return ResponseEntity.ok(medicineDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicineDTO>> searchMedicines(@RequestParam String name) {
        List<Medicine> medicines = medicineService.searchMedicinesByName(name);
        List<MedicineDTO> medicineDTOs = medicines.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(medicineDTOs);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<MedicineDTO>> getMedicinesByCategory(@PathVariable String category) {
        List<Medicine> medicines = medicineService.getMedicinesByCategory(category);
        List<MedicineDTO> medicineDTOs = medicines.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(medicineDTOs);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineDTO> createMedicine(@Valid @RequestBody MedicineDTO medicineDTO) {
        Medicine medicine = convertToEntity(medicineDTO);
        Medicine savedMedicine = medicineService.saveMedicine(medicine);
        MedicineDTO savedMedicineDTO = convertToDTO(savedMedicine);
        return ResponseEntity.ok(savedMedicineDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MedicineDTO> updateMedicine(@PathVariable String id, @Valid @RequestBody MedicineDTO medicineDTO) {
        Medicine medicine = convertToEntity(medicineDTO);
        medicine.setId(id);
        Medicine updatedMedicine = medicineService.saveMedicine(medicine);
        MedicineDTO updatedMedicineDTO = convertToDTO(updatedMedicine);
        return ResponseEntity.ok(updatedMedicineDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedicine(@PathVariable String id) {
        medicineService.deleteMedicine(id);
        return ResponseEntity.noContent().build();
    }

    private MedicineDTO convertToDTO(Medicine medicine) {
        MedicineDTO dto = new MedicineDTO();
        dto.setId(medicine.getId());
        dto.setName(medicine.getName());
        dto.setDescription(medicine.getDescription());
        dto.setManufacturer(medicine.getManufacturer());
        dto.setCategory(medicine.getCategory());
        dto.setPrice(medicine.getPrice());
        dto.setQuantityInStock(medicine.getQuantityInStock());
        dto.setMinimumStockLevel(medicine.getMinimumStockLevel());
        dto.setDosage(medicine.getDosage());
        dto.setExpiryDate(medicine.getExpiryDate());
        dto.setImageUrl(medicine.getImageUrl());
        dto.setLowStock(medicine.isLowStock());
        return dto;
    }

    private Medicine convertToEntity(MedicineDTO dto) {
        Medicine medicine = new Medicine();
        medicine.setName(dto.getName());
        medicine.setDescription(dto.getDescription());
        medicine.setManufacturer(dto.getManufacturer());
        medicine.setCategory(dto.getCategory());
        medicine.setPrice(dto.getPrice());
        medicine.setQuantityInStock(dto.getQuantityInStock());
        medicine.setMinimumStockLevel(dto.getMinimumStockLevel());
        medicine.setDosage(dto.getDosage());
        medicine.setExpiryDate(dto.getExpiryDate());
        medicine.setImageUrl(dto.getImageUrl());
        return medicine;
    }
}
