package com.server.controller;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.server.models.Domain;
import com.server.models.PublicationModel;
import com.server.models.UserModel;
import com.server.repos.DomainRepo;
import com.server.repos.PublicationRepo;
import com.server.repos.UserRepo;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private PublicationRepo publicationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DomainRepo domainRepo;

    // ---- Publications moderation ----
    @GetMapping("/publications")
    public ResponseEntity<List<PublicationModel>> listPublicationsByStatus(@RequestParam(value = "status", required = false) String status) {
        List<PublicationModel> all = publicationRepo.findAll();
        if (status == null || status.isBlank()) return ResponseEntity.ok(all);
        return ResponseEntity.ok(all.stream().filter(p -> status.equalsIgnoreCase(p.getStatus())).toList());
    }

    @PostMapping("/publications/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable("id") String id) {
        Optional<PublicationModel> pubOpt = publicationRepo.findById(id);
        if (pubOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication not found");
        PublicationModel p = pubOpt.get();
        p.setStatus("accepted");
        publicationRepo.save(p);
        return ResponseEntity.ok(p);
    }

    public static class RejectPayload { public String evaluationNote; }

    @PostMapping("/publications/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable("id") String id, @RequestBody(required = false) RejectPayload payload) {
        Optional<PublicationModel> pubOpt = publicationRepo.findById(id);
        if (pubOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication not found");
        PublicationModel p = pubOpt.get();
        p.setStatus("rejected");
        if (payload != null && payload.evaluationNote != null) p.setEvaluationNote(payload.evaluationNote);
        publicationRepo.save(p);
        return ResponseEntity.ok(p);
    }

    // ---- Domains CRUD ----
    @GetMapping("/domains")
    public List<Domain> listDomains() { return domainRepo.findAll(); }

    @PostMapping("/domains")
    public ResponseEntity<Domain> createDomain(@RequestBody Domain d) {
        Domain saved = domainRepo.save(d);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/domains/{id}")
    public ResponseEntity<?> updateDomain(@PathVariable("id") String id, @RequestBody Domain d) {
        Optional<Domain> opt = domainRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Domain not found");
        Domain existing = opt.get();
        if (d.getName() != null) existing.setName(d.getName());
        if (d.getDescription() != null) existing.setDescription(d.getDescription());
        return ResponseEntity.ok(domainRepo.save(existing));
    }

    @DeleteMapping("/domains/{id}")
    public ResponseEntity<?> deleteDomain(@PathVariable("id") String id) {
        if (!domainRepo.existsById(id)) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Domain not found");
        domainRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ---- Users management (basic) ----
    @GetMapping("/users")
    public List<UserModel> listUsers() { return userRepo.findAll(); }

    public static class StatusPayload { public String status; }

    @PostMapping("/users/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable("id") String id) {
        Optional<UserModel> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        UserModel u = userOpt.get();
        u.setStatus("active");
        return ResponseEntity.ok(userRepo.save(u));
    }

    @PostMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable("id") String id) {
        Optional<UserModel> userOpt = userRepo.findById(id);
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        UserModel u = userOpt.get();
        u.setStatus("inactive");
        return ResponseEntity.ok(userRepo.save(u));
    }

    // ---- Analytics ----
    @GetMapping("/analytics")
    public Map<String, Object> analytics() {
        long usersCount = userRepo.count();
        long publicationsCount = publicationRepo.count();
        long domainsCount = domainRepo.count();
        // naive downloads proxy (pdf present) - replace with real counter if added later
        long downloadsCount = publicationRepo.findAll().stream()
                .filter(p -> p.getPdfFileName() != null && !p.getPdfFileName().isBlank())
                .count();

        Map<String, Long> byDomain = publicationRepo.findAll().stream()
                .filter(p -> p.getDomain() != null)
                .collect(Collectors.groupingBy(PublicationModel::getDomain, Collectors.counting()));

        Map<String, Object> out = new HashMap<>();
        out.put("usersCount", usersCount);
        out.put("publicationsCount", publicationsCount);
        out.put("domainsCount", domainsCount);
        out.put("downloadsCount", downloadsCount);
        out.put("byDomain", byDomain);
        return out;
    }
}
