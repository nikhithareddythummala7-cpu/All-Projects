package com.example.table_booking.controller;

import com.example.table_booking.model.Booking;
import com.example.table_booking.model.User;
import com.example.table_booking.service.BookingService;
import com.example.table_booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    @Autowired
    private com.example.table_booking.service.AdminService adminService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getAllBookings(Model model) {
        model.addAttribute("bookings", adminService.getAllBookingsAdmin());
        return "admin/bookings";
    }

    @GetMapping("/{id}")
    public String getBookingDetail(@PathVariable("id") String id, Model model) {
        Optional<Booking> booking = bookingService.getBookingById(id);
        if (booking.isPresent()) {
            Booking b = booking.get();
            // Enrich booking with user information
            Optional<User> user = userService.getUserById(b.getUserId());
            if (user.isPresent()) {
                User u = user.get();
                // Create a map to pass enriched data
                model.addAttribute("booking", b);
                model.addAttribute("userName", u.getUsername());
                model.addAttribute("userEmail", u.getEmail());
            } else {
                model.addAttribute("booking", b);
                model.addAttribute("userName", "Unknown");
                model.addAttribute("userEmail", "N/A");
            }
            return "admin/booking-detail";
        }
        return "redirect:/admin/bookings";
    }

    @PostMapping("/{id}/delete")
    public String deleteBooking(@PathVariable("id") String id) {
        adminService.deleteBooking(id);
        return "redirect:/admin/bookings";
    }
}
