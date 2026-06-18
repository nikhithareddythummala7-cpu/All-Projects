package com.server.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.models.PublicationModel;
import com.server.repos.PublicationRepo;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private PublicationRepo publicationRepo;

    @GetMapping("/search")
    public ResponseEntity<List<PublicationModel>> search(
            @RequestParam(value = "domain", required = false) String domain,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "from", required = false) String from,
            @RequestParam(value = "to", required = false) String to
    ) {
        List<PublicationModel> all = publicationRepo.findAll();
        String ql = q == null ? null : q.toLowerCase(Locale.ROOT);
        String al = author == null ? null : author.toLowerCase(Locale.ROOT);
        final LocalDate fromDate = parseDate(from);
        final LocalDate toDate = parseDate(to);

        List<PublicationModel> filtered = all.stream()
                .filter(p -> domain == null || domain.isBlank() || Objects.equals(p.getDomain(), domain))
                .filter(p -> ql == null || containsAny(p, ql))
                .filter(p -> al == null || (p.getAuthor() != null && p.getAuthor().toLowerCase(Locale.ROOT).contains(al)))
                .filter(p -> withinDate(p.getPublishedDate(), fromDate, toDate))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filtered);
    }

    private LocalDate parseDate(String iso) {
        if (iso == null || iso.isBlank()) return null;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(iso, fmt);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean containsAny(PublicationModel p, String ql) {
        if (p.getTitle() != null && p.getTitle().toLowerCase(Locale.ROOT).contains(ql)) return true;
        if (p.getDescription() != null && p.getDescription().toLowerCase(Locale.ROOT).contains(ql)) return true;
        if (p.getKeywords() != null && p.getKeywords().stream().anyMatch(k -> k != null && k.toLowerCase(Locale.ROOT).contains(ql))) return true;
        return false;
    }

    private boolean withinDate(String publishedDate, LocalDate fromDate, LocalDate toDate) {
        if ((fromDate == null && toDate == null) || publishedDate == null || publishedDate.isBlank()) return true;
        try {
            LocalDate d = LocalDate.parse(publishedDate);
            if (fromDate != null && d.isBefore(fromDate)) return false;
            if (toDate != null && d.isAfter(toDate)) return false;
            return true;
        } catch (Exception e) {
            return true; // ignore parse failures
        }
    }
}
