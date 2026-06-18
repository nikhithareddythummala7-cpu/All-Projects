package com.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.server.repos.PublicationRepo;

@Controller
public class ViewController {

    @Autowired
    private PublicationRepo publicationRepo;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/publications")
    public String publications(Model model) {
        model.addAttribute("publications", publicationRepo.findAll());
        return "publications";
    }

    @GetMapping("/publications/{id}")
    public String publicationDetail(@PathVariable("id") String id, Model model) {
        model.addAttribute("pub", publicationRepo.findById(id).orElse(null));
        return "publication-detail";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/submit")
    public String submitPublication() {
        return "submit";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/my-publications")
    public String myPublications() {
        return "my-publications";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    // Admin pages
    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/publications")
    public String adminPublications() {
        return "admin/publications";
    }

    @GetMapping("/admin/domains")
    public String adminDomains() {
        return "admin/domains";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }

    @GetMapping("/admin/analytics")
    public String adminAnalytics() {
        return "admin/analytics";
    }
}
