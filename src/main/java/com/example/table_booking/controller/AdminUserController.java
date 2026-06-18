package com.example.table_booking.controller;

import com.example.table_booking.model.User;
import com.example.table_booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private com.example.table_booking.service.AdminService adminService;

    @GetMapping
    public String getAllUsers(Model model) {
        model.addAttribute("users", adminService.getAllUsersAdmin());
        return "admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") String id) {
        adminService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
