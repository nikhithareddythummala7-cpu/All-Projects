package com.example.medlypharma.repository;

import com.example.medlypharma.model.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem, String> {
    List<OrderItem> findByMedicineId(String medicineId);
}
