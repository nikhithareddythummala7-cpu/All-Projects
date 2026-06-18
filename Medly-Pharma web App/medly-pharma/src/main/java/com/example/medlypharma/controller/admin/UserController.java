package com.example.medlypharma.controller.admin;

import com.example.medlypharma.model.User;
import com.example.medlypharma.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/users")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        try {
            logger.info("Fetching users list - page: {}, size: {}, keyword: {}", page, size, keyword);
            
            // Get all users
            List<User> allUsers = userService.findAllUsers();
            logger.debug("Found {} total users", allUsers.size());
            
            // Apply keyword filter if provided
            List<User> filteredUsers = allUsers.stream()
                .filter(user -> {
                    boolean matches = keyword == null || keyword.isEmpty() || 
                            (user.getFullName() != null && user.getFullName().toLowerCase().contains(keyword.toLowerCase())) ||
                            (user.getEmail() != null && user.getEmail().toLowerCase().contains(keyword.toLowerCase()));
                    if (!matches) {
                        logger.debug("User filtered out - ID: {}, Name: {}, Email: {}", 
                            user.getId(), user.getFullName(), user.getEmail());
                    }
                    return matches;
                })
                .collect(Collectors.toList());
                
            logger.debug("After filtering, {} users match the criteria", filteredUsers.size());
                
            // Calculate pagination
            int totalItems = filteredUsers.size();
            int totalPages = totalItems > 0 ? (int) Math.ceil((double) totalItems / size) : 1;
            
            // Ensure page is within bounds
            page = Math.max(1, Math.min(page, totalPages));
            
            // Get the page of users
            int fromIndex = (page - 1) * size;
            int toIndex = Math.min(fromIndex + size, totalItems);
            List<User> usersPage = totalItems > 0 ? filteredUsers.subList(fromIndex, toIndex) : List.of();
            
            // Add attributes to the model
            model.addAttribute("users", usersPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("size", size);
            model.addAttribute("keyword", keyword != null ? keyword : "");
            
            // Generate page numbers for pagination
            int maxPagesToShow = 5;
            int startPage = Math.max(1, page - maxPagesToShow / 2);
            int endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);
            if (endPage - startPage < maxPagesToShow) {
                startPage = Math.max(1, endPage - maxPagesToShow + 1);
            }
            
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            
            logger.debug("Rendering users list with {} users (page {} of {})", 
                usersPage.size(), page, totalPages);
                
            return "admin/users/list";
            
        } catch (Exception e) {
            logger.error("Error fetching users list", e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "An error occurred while fetching users. Please try again later.", 
                e
            );
        }
    }
    
    @GetMapping("/{id}")
    public String viewUser(@PathVariable String id, Model model) {
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/users/view";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/users/form";
    }
    
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable String id, Model model) {
        User user = userService.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/users/form";
    }
    
    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            userService.createUser(user);
        } else {
            userService.updateUser(user.getId(), user);
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
