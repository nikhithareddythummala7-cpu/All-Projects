package com.example.medlypharma.service;

import com.example.medlypharma.model.Prescription;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PrescriptionService {
    Prescription uploadPrescription(Prescription prescription);
    Prescription uploadPrescription(MultipartFile file, String userEmail);
    Optional<Prescription> findById(String id);
    List<Prescription> findAllPrescriptions();
    List<Prescription> getAllPrescriptions();
    Prescription getPrescriptionById(String id);
    List<Prescription> getPrescriptionsByUserEmail(String userEmail);
    List<Prescription> findPrescriptionsByUserEmail(String userEmail);
    List<Prescription> findByUser(String userId);
    Prescription verifyPrescription(String id);
    Prescription rejectPrescription(String id);
    void deletePrescription(String id);
    List<Prescription> findUnverifiedPrescriptions();
    byte[] downloadPrescription(String id);
}
