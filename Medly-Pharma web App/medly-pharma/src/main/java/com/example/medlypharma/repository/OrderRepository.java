package com.example.medlypharma.repository;

import com.example.medlypharma.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    // Basic CRUD operations are inherited from MongoRepository
    
    // Find orders by user
    List<Order> findByUserId(String userId);
    List<Order> findByUserIdOrderByOrderDateDesc(String userId);
    
    // Find orders by user email
    List<Order> findByUserEmail(String userEmail);
    List<Order> findByUserEmailOrderByOrderDateDesc(String userEmail);
    
    // Find orders by status
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status);
    
    // Find orders by order number
    List<Order> findByOrderNumber(String orderNumber);
    
    // Find recent orders
    List<Order> findTop10ByOrderByOrderDateDesc();
    List<Order> findTop5ByOrderByOrderDateDesc();
    List<Order> findTop10ByOrderByCreatedAtDesc();
    
    // Find orders between dates
    @Query("{'orderDate': {$gte: ?0, $lte: ?1}}")
    List<Order> findOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find completed orders between dates
    @Query("{'status': 'COMPLETED', 'orderDate': {$gte: ?0, $lte: ?1}}")
    List<Order> findCompletedOrdersBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find orders for a specific month
    @Query("{'orderDate': {$gte: ?0, $lt: ?1}}")
    List<Order> findOrdersByMonth(LocalDateTime startOfMonth, LocalDateTime startOfNextMonth);
    
    // Count orders by status
    @Query(value = "{'status': ?0}", count = true)
    long countByStatus(Order.OrderStatus status);
    
    // Calculate monthly revenue
    @Query(value = "{'status': 'COMPLETED', 'orderDate': {$gte: ?0, $lt: ?1}}", fields = "{totalAmount: 1}")
    List<Order> findCompletedOrdersForMonth(LocalDateTime startOfMonth, LocalDateTime startOfNextMonth);
    
    @Query(value = "{$group: {_id: null, total: {$sum: '$totalAmount'}}}")
    Map<String, BigDecimal> calculateMonthlyRevenue();
    
    // Pagination
    Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
    
    // Find orders with pagination and sorting
    Page<Order> findByStatusOrderByOrderDateDesc(Order.OrderStatus status, Pageable pageable);
    
    // Find orders by user with pagination
    Page<Order> findByUserEmailOrderByOrderDateDesc(String userEmail, Pageable pageable);
    
    // Find orders by user ID with pagination
    Page<Order> findByUserIdOrderByOrderDateDesc(String userId, Pageable pageable);
}
