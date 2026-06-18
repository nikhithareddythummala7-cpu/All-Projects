package com.example.table_booking.controller;

import com.example.table_booking.model.Booking;
import com.example.table_booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantBookingApiController {

    @Autowired
    private BookingService bookingService;

    // This method was removed due to mapping conflict with RestaurantController
    // Use /api/restaurants/{restaurantId}/bookings from RestaurantController instead
}
