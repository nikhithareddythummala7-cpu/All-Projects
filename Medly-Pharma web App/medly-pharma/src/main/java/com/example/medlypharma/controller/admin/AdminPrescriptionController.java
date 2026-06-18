package com.example.medlypharma.controller.admin;

import com.example.medlypharma.model.Prescription;
import com.example.medlypharma.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/prescriptions")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminPrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping
    public String listPrescriptions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Get all prescriptions first (we'll implement pagination later)
        List<Prescription> allPrescriptions = prescriptionService.findAllPrescriptions();

        // Apply filters
        List<Prescription> filteredPrescriptions = allPrescriptions.stream()
            .filter(p -> keyword == null || keyword.isEmpty() ||
                    (p.getPatientName() != null && p.getPatientName().toLowerCase().contains(keyword.toLowerCase())) ||
                    (p.getDoctorName() != null && p.getDoctorName().toLowerCase().contains(keyword.toLowerCase())))
            .filter(p -> verified == null || p.isVerified() == verified)
            .collect(Collectors.toList());

        // For now, we'll return all prescriptions. In a real app, implement proper pagination
        model.addAttribute("prescriptions", filteredPrescriptions);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("totalItems", filteredPrescriptions.size());
        model.addAttribute("keyword", keyword);
        model.addAttribute("verified", verified);
        model.addAttribute("pageNumbers", List.of(1));

        return "admin/prescriptions/list";
    }

    @GetMapping("/{id}")
    public String viewPrescription(@PathVariable String id, Model model) {
        Prescription prescription = prescriptionService.findById(id)
            .orElseThrow(() -> new RuntimeException("Prescription not found"));
        model.addAttribute("prescription", prescription);
        return "admin/prescriptions/view";
    }

    @PostMapping("/{id}/verify")
    public String verifyPrescription(@PathVariable String id) {
        prescriptionService.verifyPrescription(id);
        return "redirect:/admin/prescriptions";
    }

    @PostMapping("/{id}/reject")
    public String rejectPrescription(@PathVariable String id) {
        prescriptionService.rejectPrescription(id);
        return "redirect:/admin/prescriptions";
    }

    @PostMapping("/{id}/delete")
    public String deletePrescription(@PathVariable String id) {
        prescriptionService.deletePrescription(id);
        return "redirect:/admin/prescriptions";
    }
}
