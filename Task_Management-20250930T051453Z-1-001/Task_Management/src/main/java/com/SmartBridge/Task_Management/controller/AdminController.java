package com.SmartBridge.Task_Management.controller;

import com.SmartBridge.Task_Management.model.User;
import com.SmartBridge.Task_Management.repository.UserRepository;
import com.SmartBridge.Task_Management.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskService taskService;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable String id, @RequestParam("role") String role) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            User u = opt.get();
            u.setRole(role);
            userRepository.save(u);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable String id, Authentication authentication) {
        // Optional: prevent deleting yourself to avoid lockout
        Optional<User> target = userRepository.findById(id);
        if (target.isPresent()) {
            User t = target.get();
            if (authentication != null && t.getUsername().equals(authentication.getName())) {
                return "redirect:/admin/users?error=cannot_delete_self";
            }
            userRepository.deleteById(id);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/tasks")
    public String allTasks(Model model) {
        model.addAttribute("tasks", taskService.getAllTasks());
        return "admin/tasks";
    }
}
