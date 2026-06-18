package com.example.table_booking.repository;

import com.example.table_booking.model.Table;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends MongoRepository<Table, String> {

    List<Table> findByRestaurantId(String restaurantId);

    List<Table> findByRestaurantIdAndCapacityGreaterThanEqual(String restaurantId, int capacity);
}
