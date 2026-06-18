package com.groceryapp.freshcart.repository;

import com.groceryapp.freshcart.model.Order;
import com.groceryapp.freshcart.model.User;
import com.groceryapp.freshcart.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    // Get all orders for a specific user, sorted by orderDate descending
    List<Order> findByUserIdOrderByOrderDateDesc(String userId);

    // Get all orders with a specific status, sorted by orderDate descending
    List<Order> findByStatusOrderByOrderDateDesc(OrderStatus status);

    // Get all orders sorted by orderDate descending
    List<Order> findAllByOrderByOrderDateDesc();

    // Get a specific order by id and user
    Optional<Order> findByIdAndUserId(String id, String userId);
}
