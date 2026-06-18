package com.groceryapp.freshcart.repository;

import com.groceryapp.freshcart.model.OrderItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends MongoRepository<OrderItem, String> {
    // You can add custom queries here if needed, e.g. findByOrderId
}
