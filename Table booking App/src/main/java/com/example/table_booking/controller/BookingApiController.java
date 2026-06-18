package com.example.table_booking.controller;

import com.example.table_booking.dto.BookingDTO;
import com.example.table_booking.model.Booking;
import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.User;
import com.example.table_booking.service.BookingService;
import com.example.table_booking.service.RestaurantService;
import com.example.table_booking.service.UserService;
import com.example.table_booking.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;


    // CREATE BOOKING ---------------------------------------------------------

    @GetMapping("/create")
    public ResponseEntity<?> createBookingGet() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Collections.singletonMap("error", "Please use POST request for creating bookings"));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(
            @RequestParam String tableId,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false, defaultValue = "1") int numberOfGuests,
            HttpServletRequest request) {

        try {
            String token = extractToken(request);
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Authentication required - no token found"));
            }

            String username;
            try {
                username = jwtUtils.extractUsername(token);
            } catch (Exception tokenEx) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Invalid or expired token"));
            }

            Optional<User> userOpt = userService.getUserByEmail(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);

            Booking booking = bookingService.createBooking(
                    tableId,
                    userOpt.get().getId(),
                    start,
                    end,
                    numberOfGuests
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(booking);

        } catch (RuntimeException re) {
            return ResponseEntity.status(409)
                    .body(Collections.singletonMap("error", re.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Booking Failed: " + e.getMessage()));
        }
    }


    // CONFIRM BOOKING --------------------------------------------------------

    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable String id, HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            String username = jwtUtils.extractUsername(token);
            Optional<User> userOpt = userService.getUserByEmail(username);
            if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            User owner = userOpt.get();
            Optional<Booking> bOpt = bookingService.getBookingById(id);
            if (bOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");

            Booking booking = bOpt.get();

            Optional<Restaurant> restOpt =
                    restaurantService.findRestaurantByOwner(owner.getEmail());

            if (restOpt.isEmpty() ||
                    !restOpt.get().getId().equals(booking.getRestaurantId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            booking.setStatus("CONFIRMED");
            bookingService.updateBooking(booking);

            return ResponseEntity.ok(Collections.singletonMap("status", "CONFIRMED"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    // CANCEL BOOKING ---------------------------------------------------------

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String id, HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            String username = jwtUtils.extractUsername(token);
            Optional<User> userOpt = userService.getUserByEmail(username);
            if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            User caller = userOpt.get();
            Optional<Booking> bOpt = bookingService.getBookingById(id);
            if (bOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found");

            Booking booking = bOpt.get();

            boolean allowed = booking.getUserId().equals(caller.getId());
            if (!allowed) {
                Optional<Restaurant> restOpt = restaurantService.findRestaurantByOwner(caller.getEmail());
                allowed = restOpt.isPresent() &&
                          restOpt.get().getId().equals(booking.getRestaurantId());
            }

            if (!allowed)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");

            booking.setStatus("CANCELLED");
            bookingService.updateBooking(booking);

            return ResponseEntity.ok(Collections.singletonMap("status", "CANCELLED"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    // USER BOOKINGS ----------------------------------------------------------

    @GetMapping("/my")
    public ResponseEntity<?> getUserBookings(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Authentication required"));

            String username = jwtUtils.extractUsername(token);
            Optional<User> userOpt = userService.getUserByEmail(username);
            if (userOpt.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "User not found"));

            List<Booking> bookings =
                    bookingService.getBookingsByUserId(userOpt.get().getId());

            return ResponseEntity.ok(bookings);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }


    // ⭐ TABLE AVAILABILITY CHECK (PUBLIC) ------------------------------------

    @GetMapping("/table/{tableId}")
    public ResponseEntity<?> getTableBookings(@PathVariable String tableId) {
        try {
            List<Booking> bookings = bookingService.getBookingsByTableId(tableId);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            List<BookingDTO> dtos = bookings.stream()
                .map(b -> new BookingDTO(
                    b.getId(),
                    b.getTableId(),
                    b.getRestaurantId(),
                    b.getStartTime() != null ? b.getStartTime().format(formatter) : null,
                    b.getEndTime() != null ? b.getEndTime().format(formatter) : null,
                    b.getNumberOfGuests(),
                    b.getStatus()
                ))
                .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to retrieve bookings: " + e.getMessage()));
        }
    }


    // TOKEN EXTRACTION -------------------------------------------------------

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer "))
            return authHeader.substring(7);

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies())
                if ("jwt".equals(cookie.getName()))
                    return cookie.getValue();
        }
        return null;
    }
}
