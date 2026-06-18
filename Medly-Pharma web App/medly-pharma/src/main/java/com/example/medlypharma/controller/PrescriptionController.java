package com.example.medlypharma.controller;

import com.example.medlypharma.dto.PrescriptionDTO;
import com.example.medlypharma.model.Prescription;
import com.example.medlypharma.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PrescriptionDTO>> getAllPrescriptions() {
        List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
        List<PrescriptionDTO> prescriptionDTOs = prescriptions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(prescriptionDTOs);
    }

    @GetMapping("/my-prescriptions")
    public ResponseEntity<List<PrescriptionDTO>> getUserPrescriptions(Authentication authentication) {
        String email = authentication.getName();
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByUserEmail(email);
        List<PrescriptionDTO> prescriptionDTOs = prescriptions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(prescriptionDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @prescriptionService.isPrescriptionOwner(#id, authentication.name)")
    public ResponseEntity<PrescriptionDTO> getPrescriptionById(@PathVariable String id) {
        Prescription prescription = prescriptionService.getPrescriptionById(id);
        PrescriptionDTO prescriptionDTO = convertToDTO(prescription);
        return ResponseEntity.ok(prescriptionDTO);
    }

    @PostMapping("/upload")
    public ResponseEntity<PrescriptionDTO> uploadPrescription(@RequestParam("file") MultipartFile file, Authentication authentication) {
        String email = authentication.getName();
        Prescription prescription = prescriptionService.uploadPrescription(file, email);
        PrescriptionDTO prescriptionDTO = convertToDTO(prescription);
        return ResponseEntity.ok(prescriptionDTO);
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('ADMIN') or @prescriptionService.isPrescriptionOwner(#id, authentication.name)")
    public ResponseEntity<byte[]> downloadPrescription(@PathVariable String id) {
        byte[] fileData = prescriptionService.downloadPrescription(id);
        Prescription prescription = prescriptionService.getPrescriptionById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(prescription.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + prescription.getFileName() + "\"")
                .body(fileData);
    }

    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PrescriptionDTO> verifyPrescription(@PathVariable String id) {
        Prescription verifiedPrescription = prescriptionService.verifyPrescription(id);
        PrescriptionDTO prescriptionDTO = convertToDTO(verifiedPrescription);
        return ResponseEntity.ok(prescriptionDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @prescriptionService.isPrescriptionOwner(#id, authentication.name)")
    public ResponseEntity<Void> deletePrescription(@PathVariable String id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }

    private PrescriptionDTO convertToDTO(Prescription prescription) {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId(prescription.getId());
        dto.setFileName(prescription.getFileName());
        dto.setFilePath(prescription.getFilePath());
        dto.setFileType(prescription.getFileType());
        dto.setFileSize(prescription.getFileSize());
        dto.setUploadDate(prescription.getUploadDate());
        dto.setVerified(prescription.isVerified());
        dto.setPatientName(prescription.getPatientName());
        dto.setDoctorName(prescription.getDoctorName());
        if (prescription.getUser() != null) {
            dto.setUserId(prescription.getUser().getId());
            dto.setUserFullName(prescription.getUser().getFullName());
        } else {
            dto.setUserId(null);
            dto.setUserFullName("Unknown");
        }
        dto.setDownloadUrl(prescription.getDownloadUrl());
        return dto;
    }
}
