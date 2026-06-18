package com.server.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.server.models.PublicationModel;
import com.server.models.UserModel;
import com.server.repos.PublicationRepo;
import com.server.repos.UserRepo;
import com.server.service.FileUploadService;

import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class RouteController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    PublicationRepo publicationRepo;

    @PostMapping("/register")
    public ResponseEntity<?> registerMethod(@RequestBody UserModel userData) {
        try {
            if (userData == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body");
            }
            String email = userData.getEmail();
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
            }
            // Optional: prevent duplicate emails if repo method exists
            UserModel existing = userRepo.findByEmail(email);
            if (existing != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
            }
            UserModel saved = userRepo.save(userData);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginMethod(@RequestBody UserModel userData) {
        try {
            if (userData == null || userData.getEmail() == null || userData.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and password are required");
            }
            UserModel user = userRepo.findByEmail(userData.getEmail());
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
            if (user.getPassword() != null && user.getPassword().equals(userData.getPassword())) {
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed: " + e.getMessage());
        }
    }

    private final FileUploadService fileUploadService;

    public RouteController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @Value("${file.upload-dir}")
    private String uploadPath;

    @PostMapping("/new-publication")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, 
                                        @RequestParam("title") String title,
                                        @RequestParam("description") String description,
                                        @RequestParam("bannerImg") String bannerImg,
                                        @RequestParam("domain") String domain,
                                        @RequestParam("keywords") List<String> keywords,
                                        @RequestParam("author") String author,
                                        @RequestParam("authorId") String authorId,
                                        @RequestParam("publishedDate") String publishedDate) throws IOException {
        
        String uniqueFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // Ensure directory exists
        Path uploadDir = Paths.get(uploadPath);
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());

        PublicationModel publication = new PublicationModel();
        publication.setTitle(title);
        publication.setDescription(description);
        publication.setBannerImg(bannerImg);
        publication.setDomain(domain);
        publication.setKeywords(keywords);
        publication.setAuthor(author);
        publication.setAuthorId(authorId);
        publication.setPublishedDate(publishedDate);
        publication.setPdfFileName(uniqueFileName);
        publication.setStatus("pending");

        publicationRepo.save(publication);

        return ResponseEntity.ok("File uploaded successfully!");
    }

    @GetMapping("/fetch-publications")
    public List<PublicationModel> getPublications() {

        List<PublicationModel> publications = publicationRepo.findAll();
        return publications;
    }
    
    @GetMapping("/fetch-publication/{id}")
    public ResponseEntity<?> getPublication(@PathVariable("id") String id) {
        Optional<PublicationModel> pub = publicationRepo.findById(id);
        if (pub.isPresent()) {
            return ResponseEntity.ok(pub.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication not found");
    }

    @PostMapping("/approve-publication/{id}")
    public ResponseEntity<?> approvePublication(@PathVariable("id") String id, @RequestBody PublicationModel evaluator) {
        Optional<PublicationModel> pubOpt = publicationRepo.findById(id);
        if (pubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication not found");
        }
        PublicationModel pub = pubOpt.get();
        pub.setStatus("accepted");
        pub.setEvaluator(evaluator.getEvaluator());
        pub.setEvaluatorId(evaluator.getEvaluatorId());
        pub.setEvaluationDate(evaluator.getEvaluationDate());
        pub.setEvaluationNote(evaluator.getEvaluationNote());
        PublicationModel updated = publicationRepo.save(pub);
        return ResponseEntity.ok(updated);
    }
    


    @GetMapping("/fetch-users")
    public List<UserModel> getUsers() {

        List<UserModel> users = userRepo.findAll();
        return users;
    }


    
}
