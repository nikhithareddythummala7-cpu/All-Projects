package com.example.table_booking.repository;

import com.example.table_booking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByUserId(String userId);

    List<Booking> findByTableId(String tableId);

    List<Booking> findByTableIdAndStartTimeBetween(String tableId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByUserIdAndStatus(String userId, String status);

    List<Booking> findByStatus(String status);

    List<Booking> findByRestaurantId(String restaurantId);



    List<Booking> findByRestaurantIdAndStartTimeBetween(
        String restaurantId,
        LocalDateTime start,
        LocalDateTime end
);

}
