package com.example.medlypharma.controller.admin;

import com.example.medlypharma.model.Medicine;
import com.example.medlypharma.service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.dao.DataAccessException;

@Controller
@RequestMapping("/admin/medicines")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMedicineController {

    private final MedicineService medicineService;

    @GetMapping("")
    public String listMedicines(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            Model model) {
        try {
            List<Medicine> medicines;
            if (keyword != null && !keyword.trim().isEmpty()) {
                medicines = medicineService.searchMedicinesByName(keyword);
            } else if (category != null && !category.trim().isEmpty()) {
                medicines = medicineService.findByCategory(category);
            } else {
                medicines = medicineService.findAllMedicines();
            }
            
            model.addAttribute("medicines", medicines != null ? medicines : new ArrayList<>());
            model.addAttribute("categories", populateCategories());
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("selectedCategory", category);
            
            return "admin/medicines/list";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error loading medicines");
            model.addAttribute("medicines", new ArrayList<>());
            model.addAttribute("categories", populateCategories());
            return "admin/medicines/list";
        }
    }

    @GetMapping("/new")
    public String showNewMedicineForm(Model model) {
        if (!model.containsAttribute("medicine")) {
            model.addAttribute("medicine", new Medicine());
        }
        model.addAttribute("categories", populateCategories());
        model.addAttribute("manufacturers", populateManufacturers());
        return "admin/medicines/form";
    }

    @GetMapping("/edit/{id}")
    public String editMedicine(@PathVariable String id, Model model, RedirectAttributes redirectAttrs) {
        try {
            Medicine medicine = medicineService.findById(id)
                .orElse(null);
            if (medicine == null) {
                redirectAttrs.addFlashAttribute("error", "Medicine not found");
                return "redirect:/admin/medicines";
            }
            model.addAttribute("medicine", medicine);
            model.addAttribute("categories", populateCategories());
            model.addAttribute("manufacturers", populateManufacturers());
            return "admin/medicines/form";
        } catch (DataAccessException e) {
            redirectAttrs.addFlashAttribute("error", "Error accessing medicine");
            return "redirect:/admin/medicines";
        }
    }

    @PostMapping("/save")
    public String saveMedicine(
            @Valid @ModelAttribute("medicine") Medicine medicine,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttrs) {
        
        // Add model attributes needed for the form
        model.addAttribute("categories", populateCategories());
        model.addAttribute("manufacturers", populateManufacturers());
        
        // Custom validation for expiry date
        if (medicine.getExpiryDate() != null && 
            medicine.getExpiryDate().isBefore(LocalDate.now())) {
            result.rejectValue("expiryDate", "error.medicine", "Expiry date must be in the future");
        }
        
        // Custom validation for stock
        if (medicine.getQuantityInStock() != null && medicine.getMinimumStockLevel() != null &&
            medicine.getQuantityInStock() < medicine.getMinimumStockLevel()) {
            result.rejectValue("quantityInStock", "error.medicine", 
                "Quantity in stock must be greater than or equal to minimum stock level");
        }

        // Check for validation errors
        if (result.hasErrors()) {
            System.out.println("Validation errors: " + result.getAllErrors());
            model.addAttribute("errorMessage", "Please fix the validation errors below");
            return "admin/medicines/form";
        }
        
        try {
            Medicine savedMedicine;
            if (medicine.getId() == null || medicine.getId().trim().isEmpty()) {
                medicine.setId(null); // Let MongoDB generate ID
                medicine.setCreatedAt(LocalDateTime.now());
                medicine.setUpdatedAt(LocalDateTime.now());
                savedMedicine = medicineService.createMedicine(medicine);
                redirectAttrs.addFlashAttribute("success", "Medicine added successfully");
            } else {
                medicine.setUpdatedAt(LocalDateTime.now());
                savedMedicine = medicineService.updateMedicine(medicine.getId(), medicine);
                redirectAttrs.addFlashAttribute("success", "Medicine updated successfully");
            }
            
            if (savedMedicine == null) {
                model.addAttribute("errorMessage", "Error saving medicine");
                return "admin/medicines/form";
            }
            
        } catch (Exception e) {
            System.err.println("Error saving medicine: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Error saving medicine: " + e.getMessage());
            return "admin/medicines/form";
        }
        
        return "redirect:/admin/medicines";
    }

    @GetMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable String id, RedirectAttributes redirectAttrs) {
        try {
            if (id == null || id.trim().isEmpty()) {
                redirectAttrs.addFlashAttribute("error", "Invalid medicine ID");
                return "redirect:/admin/medicines";
            }
            
            Medicine medicine = medicineService.findById(id)
                .orElse(null);
            if (medicine == null) {
                redirectAttrs.addFlashAttribute("error", "Medicine not found");
                return "redirect:/admin/medicines";
            }
            
            medicineService.deleteMedicine(id);
            redirectAttrs.addFlashAttribute("success", "Medicine deleted successfully");
            
        } catch (DataAccessException e) {
            redirectAttrs.addFlashAttribute("error", "Error deleting medicine");
        }
        return "redirect:/admin/medicines";
    }
    
    private List<String> populateCategories() {
        return Arrays.asList(
            "Antibiotic", 
            "Painkiller", 
            "Vitamin", 
            "Antihistamine", 
            "Antacid", 
            "Antidepressant",
            "Antifungal",
            "Antiviral",
            "Blood Pressure",
            "Diabetes",
            "Other"
        );
    }
    
    private List<String> populateManufacturers() {
        return Arrays.asList(
            "Sun Pharma",
            "Cipla",
            "Dr. Reddy's",
            "Lupin",
            "Zydus Cadila",
            "Other"
        );
    }
}