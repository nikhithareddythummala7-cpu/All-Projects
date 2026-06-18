package com.example.table_booking.service;

import com.example.table_booking.model.Booking;
import com.example.table_booking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.example.table_booking.model.Restaurant;
import com.example.table_booking.model.Table;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TableService tableService;

    @Autowired
    private RestaurantService restaurantService;


    public Booking createBooking(String tableId, String userId,
                                 LocalDateTime startTime, LocalDateTime endTime,
                                 int numberOfGuests) {
        // Load table
        Table table = tableService.getTableById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        // Load restaurant using the table's restaurantId
        Restaurant restaurant = restaurantService.getRestaurantById(table.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Ensure table is available
        String status = table.getStatus();
        if (status == null || status.trim().isEmpty()) {
            status = "AVAILABLE"; // fallback for old DB values
        }

        if (!status.equalsIgnoreCase("AVAILABLE")) {
            throw new RuntimeException("This table is not available for booking.");
        }

        // Check overlapping bookings
        if (isTimeSlotBooked(tableId, startTime, endTime)) {
            throw new RuntimeException("Time slot already booked");
        }

        // Build booking and populate required fields
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setTableId(tableId);

        // Set restaurant and table info so DB records are complete
        booking.setRestaurantId(restaurant.getId());
        booking.setRestaurantName(restaurant.getName());
        booking.setTableNumber(table.getTableNumber());

        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus("PENDING");
        booking.setNumberOfGuests(numberOfGuests);

        return bookingRepository.save(booking);
    }

    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Optional<Booking> getBookingById(String id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking updateBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public void deleteBooking(String id) {
        try {
            bookingRepository.deleteById(id);
            logger.info("Deleted booking with id: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting booking with id: {}", id, e);
            throw e;
        }
    }

    public List<Booking> getBookingsByRestaurantId(String restaurantId) {
        return bookingRepository.findByRestaurantId(restaurantId);
    }

    public List<Booking> getBookingsByUserId(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsByTableId(String tableId) {
        return bookingRepository.findByTableId(tableId);
    }

    public boolean isTimeSlotBooked(String tableId, LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = bookingRepository.findByTableId(tableId);

        for (Booking b : bookings) {
            if (b.getStatus() != null && b.getStatus().equalsIgnoreCase("CANCELLED")) {
                continue;
            }

            // overlap if start < existing.end && end > existing.start
            LocalDateTime exStart = b.getStartTime();
            LocalDateTime exEnd = b.getEndTime();
            if (exStart == null || exEnd == null) continue; // defensive

            boolean overlap = start.isBefore(exEnd) && end.isAfter(exStart);
            if (overlap) {
                return true;
            }
        }
        return false;
    }
}
