package com.server.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.models.PublicationModel;
import com.server.models.UserModel;
import com.server.repos.PublicationRepo;
import com.server.repos.UserRepo;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PublicationRepo publicationRepo;

    // Fetch current user profile
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestParam("userId") String userId) {
        Optional<UserModel> user = userRepo.findById(userId);
        return user.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
    }

    // Update current user profile
    @PutMapping("/me")
    public ResponseEntity<?> updateMe(@RequestParam("userId") String userId, @RequestBody UserModel payload) {
        Optional<UserModel> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        UserModel u = userOpt.get();
        if (payload.getUsername() != null) u.setUsername(payload.getUsername());
        if (payload.getEmail() != null) u.setEmail(payload.getEmail());
        if (payload.getAbout() != null) u.setAbout(payload.getAbout());
        if (payload.getAvatarUrl() != null) u.setAvatarUrl(payload.getAvatarUrl());
        if (payload.getDomain() != null) u.setDomain(payload.getDomain());
        if (payload.getQualification() != null) u.setQualification(payload.getQualification());
        if (payload.getStatus() != null) u.setStatus(payload.getStatus());
        UserModel saved = userRepo.save(u);
        return ResponseEntity.ok(saved);
    }

    // List publications submitted by the current user
    @GetMapping("/my-publications")
    public ResponseEntity<List<PublicationModel>> myPublications(@RequestParam("ownerId") String ownerId) {
        // Using authorId as ownerId
        List<PublicationModel> list = publicationRepo.findByAuthorId(ownerId);
        return ResponseEntity.ok(list);
    }

    // Edit publication (only by owner)
    @PutMapping("/publications/{id}")
    public ResponseEntity<?> editPublication(@PathVariable("id") String id,
                                             @RequestParam("ownerId") String ownerId,
                                             @RequestBody PublicationModel payload) {
        Optional<PublicationModel> pubOpt = publicationRepo.findById(id);
        if (pubOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication not found");
        PublicationModel p = pubOpt.get();
        if (p.getAuthorId() == null || !p.getAuthorId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed");
        }
        if (payload.getTitle() != null) p.setTitle(payload.getTitle());
        if (payload.getDescription() != null) p.setDescription(payload.getDescription());
        if (payload.getDomain() != null) p.setDomain(payload.getDomain());
        if (payload.getKeywords() != null) p.setKeywords(payload.getKeywords());
        if (payload.getBannerImg() != null) p.setBannerImg(payload.getBannerImg());
        PublicationModel saved = publicationRepo.save(p);
        return ResponseEntity.ok(saved);
    }

    // Delete publication (only by owner)
    @DeleteMapping("/publications/{id}")
    public ResponseEntity<?> deletePublication(@PathVariable("id") String id,
                                               @RequestParam("ownerId") String ownerId) {
        Optional<PublicationModel> pubOpt = publicationRepo.findById(id);
        if (pubOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication not found");
        PublicationModel p = pubOpt.get();
        if (p.getAuthorId() == null || !p.getAuthorId().equals(ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not allowed");
        }
        publicationRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
