package com.example.table_booking.controller;

import com.example.table_booking.service.BookingService;
import com.example.table_booking.service.UserService;
import com.example.table_booking.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookingViewController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/my-bookings")
    public String showMyBookingsPage(HttpServletRequest request, Model model) {

        // Extract token
        String token = null;
        var header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        } else if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt")) {
                    token = cookie.getValue();
                }
            }
        }

        if (token != null) {
            String username = jwtUtils.extractUsername(token);
            var userOpt = userService.getUserByEmail(username);

            if (userOpt.isPresent()) {
                var user = userOpt.get();
                model.addAttribute("bookings", bookingService.getBookingsByUserId(user.getId()));
            }
        }

        return "myBookings";
    }

    @GetMapping("/bookings/my")
    public String showMyBookingsPageAlternate(HttpServletRequest request, Model model) {
        return showMyBookingsPage(request, model);
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable String id, HttpServletRequest request) {
        try {
            String token = null;
            var header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                token = header.substring(7);
            } else if (request.getCookies() != null) {
                for (var cookie : request.getCookies()) {
                    if (cookie.getName().equals("jwt")) {
                        token = cookie.getValue();
                    }
                }
            }

            if (token != null) {
                String username = jwtUtils.extractUsername(token);
                var userOpt = userService.getUserByEmail(username);

                if (userOpt.isPresent()) {
                    var booking = bookingService.getBookingById(id);
                    if (booking.isPresent() && booking.get().getUserId().equals(userOpt.get().getId())) {
                        var b = booking.get();
                        b.setStatus("CANCELLED");
                        bookingService.updateBooking(b);
                    }
                }
            }
        } catch (Exception e) {
            // Log error silently
        }

        return "redirect:/bookings/my";
    }
}