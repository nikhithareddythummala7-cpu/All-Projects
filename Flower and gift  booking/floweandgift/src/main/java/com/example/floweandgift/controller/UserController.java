package com.example.floweandgift.controller;

import com.example.floweandgift.model.Address;
import com.example.floweandgift.model.User;
import com.example.floweandgift.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Optional<User> user = userService.findByUsername(userDetails.getUsername());
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/profile";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute User updatedUser, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Optional<User> existingUser = userService.findByUsername(userDetails.getUsername());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setPhone(updatedUser.getPhone());
            user.setEmail(updatedUser.getEmail());
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found");
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/addresses")
    public String manageAddresses(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Optional<User> user = userService.findByUsername(userDetails.getUsername());
        if (user.isPresent()) {
            model.addAttribute("addresses", user.get().getAddresses());
            model.addAttribute("address", new Address());
            return "user/addresses";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/addresses")
    public String addOrUpdateAddress(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute Address address, @RequestParam(required = false) Integer index, RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        Optional<User> existingUser = userService.findByUsername(userDetails.getUsername());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            List<Address> addresses = user.getAddresses() != null ? user.getAddresses() : new ArrayList<>();
            if (index != null && index >= 0 && index < addresses.size()) {
                addresses.set(index, address);
                redirectAttributes.addFlashAttribute("message", "Address updated successfully");
            } else {
                addresses.add(address);
                redirectAttributes.addFlashAttribute("message", "Address added successfully");
            }
            user.setAddresses(addresses);
            userService.updateUser(user);
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found");
        }
        return "redirect:/user/addresses";
    }
}
