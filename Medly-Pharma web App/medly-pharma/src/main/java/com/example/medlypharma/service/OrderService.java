package com.example.medlypharma.service;

import com.example.medlypharma.model.Order;
import com.example.medlypharma.model.OrderItem;
import com.example.medlypharma.model.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    // Basic CRUD operations
    Order createOrder(String userEmail, List<OrderItem> items);
    Order createOrder(Order order);
    Optional<Order> findById(String id);
    List<Order> findAllOrders();
    List<Order> getAllOrders();
    Order getOrderById(String id);
    Order updateOrderStatus(String orderId, OrderStatus status);
    void deleteOrder(String id);
    
    // Query methods
    List<Order> getOrdersByUserEmail(String userEmail);
    List<Order> findOrdersByUserEmail(String userEmail);
    List<Order> findOrdersByUser(String userId);
    List<Order> findOrdersByStatus(OrderStatus status);
    
    // Dashboard methods
    long countAllOrders();
    BigDecimal calculateTotalRevenue();
    List<Order> findRecentOrders(int limit);
    Page<Order> findRecentOrders(Pageable pageable);
    
    // Analytics methods
    BigDecimal calculateRevenueForDate(LocalDate date);
    BigDecimal calculateRevenueForMonth(int year, int month);
    BigDecimal calculateRevenueForPeriod(LocalDate startDate, LocalDate endDate);
    Map<String, Long> getOrderStatusCounts();
    Map<String, Long> getOrdersByMonth();
    Map<String, Double> getRevenueByMonth();
    
    // Order operations
    BigDecimal calculateOrderTotal(List<OrderItem> items);
    Order addItemToOrder(String orderId, OrderItem item);
    Order removeItemFromOrder(String orderId, String itemId);
    boolean isStockAvailable(String medicineId, int quantity);
    void updateStockAfterOrder(Order order);
    boolean isOrderOwner(String orderId, String userEmail);
}
