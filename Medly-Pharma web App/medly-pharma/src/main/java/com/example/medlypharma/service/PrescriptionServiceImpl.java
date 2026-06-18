package com.example.medlypharma.service;

import com.example.medlypharma.model.Prescription;
import com.example.medlypharma.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final String uploadDir = "uploads/prescriptions/";

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
        // Create upload directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Override
    public Prescription uploadPrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    @Override
    public Prescription uploadPrescription(MultipartFile file, String userEmail) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            Prescription prescription = new Prescription();
            prescription.setFileName(fileName);
            prescription.setFilePath(filePath.toString());
            prescription.setFileSize(file.getSize());
            prescription.setContentType(file.getContentType());
            prescription.setUserEmail(userEmail);
            prescription.setVerified(false);

            // Set the user reference if needed, but since we have userEmail, it might be sufficient
            // If User entity is needed, fetch it from UserService
            return prescriptionRepository.save(prescription);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload prescription", e);
        }
    }

    @Override
    public Optional<Prescription> findById(String id) {
        return prescriptionRepository.findById(id);
    }

    @Override
    public List<Prescription> findAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    @Override
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    @Override
    public List<Prescription> findPrescriptionsByUserEmail(String userEmail) {
        return prescriptionRepository.findByUserEmail(userEmail);
    }

    @Override
    public List<Prescription> findByUser(String userId) {
        return prescriptionRepository.findByUserId(userId);
    }

    @Override
    public Prescription verifyPrescription(String id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found"));
        prescription.setVerified(true);
        return prescriptionRepository.save(prescription);
    }

    @Override
    public Prescription rejectPrescription(String id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found"));
        prescription.setVerified(false);
        return prescriptionRepository.save(prescription);
    }

    @Override
    public void deletePrescription(String id) {
        prescriptionRepository.deleteById(id);
    }

    @Override
    public List<Prescription> findUnverifiedPrescriptions() {
        return prescriptionRepository.findByVerified(false);
    }

    @Override
    public byte[] downloadPrescription(String id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found"));
        try {
            Path filePath = Paths.get(prescription.getFilePath());
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read prescription file", e);
        }
    }

    @Override
    public Prescription getPrescriptionById(String id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Prescription not found"));
    }

    @Override
    public List<Prescription> getPrescriptionsByUserEmail(String userEmail) {
        return findPrescriptionsByUserEmail(userEmail);
    }
}
