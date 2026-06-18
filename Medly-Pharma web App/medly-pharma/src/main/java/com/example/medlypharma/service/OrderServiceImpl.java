package com.example.medlypharma.service;

import com.example.medlypharma.model.Medicine;
import com.example.medlypharma.model.Order;
import com.example.medlypharma.model.OrderItem;
import com.example.medlypharma.model.Order.OrderStatus;
import com.example.medlypharma.repository.MedicineRepository;
import com.example.medlypharma.repository.OrderItemRepository;
import com.example.medlypharma.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MedicineRepository medicineRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, MedicineRepository medicineRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.medicineRepository = medicineRepository;
    }

    // Analytics methods
    @Override
    public long countAllOrders() {
        return orderRepository.count();
    }
    
    @Override
    public BigDecimal calculateTotalRevenue() {
        List<Order> deliveredOrders = orderRepository.findByStatus(Order.OrderStatus.DELIVERED);
        return deliveredOrders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateRevenueForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findCompletedOrdersBetweenDates(startOfDay, endOfDay);
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateRevenueForMonth(int year, int month) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime startOfNextMonth = startOfMonth.plusMonths(1);
        List<Order> orders = orderRepository.findCompletedOrdersBetweenDates(startOfMonth, startOfNextMonth);
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal calculateRevenueForPeriod(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        List<Order> orders = orderRepository.findCompletedOrdersBetweenDates(startDateTime, endDateTime);
        return orders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<String, Long> getOrderStatusCounts() {
        Map<String, Long> statusCounts = new HashMap<>();
        for (Order.OrderStatus status : Order.OrderStatus.values()) {
            long count = orderRepository.countByStatus(status);
            statusCounts.put(status.toString(), count);
        }
        return statusCounts;
    }

    @Override
    public Map<String, Long> getOrdersByMonth() {
        Map<String, Long> ordersByMonth = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        // Initialize last 12 months
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            String monthKey = yearMonth.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            ordersByMonth.put(monthKey, 0L);
        }

        // Get all orders and group by month
        List<Order> allOrders = orderRepository.findAll();
        for (Order order : allOrders) {
            if (order.getOrderDate() != null) {
                YearMonth orderMonth = YearMonth.from(order.getOrderDate().toLocalDate());
                String monthKey = orderMonth.format(DateTimeFormatter.ofPattern("MMM yyyy"));
                if (ordersByMonth.containsKey(monthKey)) {
                    ordersByMonth.put(monthKey, ordersByMonth.get(monthKey) + 1);
                }
            }
        }

        return ordersByMonth;
    }

    @Override
    public Map<String, Double> getRevenueByMonth() {
        Map<String, Double> revenueByMonth = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        // Initialize last 12 months
        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
            String monthKey = yearMonth.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            revenueByMonth.put(monthKey, 0.0);
        }

        // Get completed orders and calculate revenue by month
        List<Order> completedOrders = orderRepository.findByStatus(Order.OrderStatus.DELIVERED);
        for (Order order : completedOrders) {
            if (order.getOrderDate() != null && order.getTotalAmount() != null) {
                YearMonth orderMonth = YearMonth.from(order.getOrderDate().toLocalDate());
                String monthKey = orderMonth.format(DateTimeFormatter.ofPattern("MMM yyyy"));
                if (revenueByMonth.containsKey(monthKey)) {
                    double currentRevenue = revenueByMonth.get(monthKey);
                    revenueByMonth.put(monthKey, currentRevenue + order.getTotalAmount().doubleValue());
                }
            }
        }

        return revenueByMonth;
    }
    
    // Query methods
    @Override
    public List<Order> findRecentOrders(int limit) {
        return orderRepository.findTop10ByOrderByOrderDateDesc()
                .stream()
                .limit(limit)
                .toList();
    }
    
    @Override
    public Page<Order> findRecentOrders(Pageable pageable) {
        return orderRepository.findAllByOrderByOrderDateDesc(pageable);
    }

    @Override
    public Order createOrder(Order order) {
        // Generate order number
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Calculate total amount
        order.setTotalAmount(calculateOrderTotal(order.getItems().stream().toList()));

        // Check stock availability
        for (OrderItem item : order.getItems()) {
            if (!isStockAvailable(item.getMedicine().getId(), item.getQuantity())) {
                throw new IllegalArgumentException("Insufficient stock for medicine: " + item.getMedicine().getName());
            }
        }

        // Save order items first
        for (OrderItem item : order.getItems()) {
            orderItemRepository.save(item);
        }

        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order createOrder(String userEmail, List<OrderItem> items) {
        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        // Set unitPrice for each OrderItem from medicine price
        for (OrderItem item : items) {
            item.setUnitPrice(item.getMedicine().getPrice());
        }

        order.setItems(new HashSet<>(items));
        order.setTotalAmount(calculateOrderTotal(items));

        // Check stock availability
        for (OrderItem item : items) {
            if (!isStockAvailable(item.getMedicine().getId(), item.getQuantity())) {
                throw new IllegalArgumentException("Insufficient stock for medicine: " + item.getMedicine().getName());
            }
        }

        // Save order items first
        for (OrderItem item : items) {
            orderItemRepository.save(item);
        }

        return orderRepository.save(order);
    }

    @Override
    public List<Order> findOrdersByUserEmail(String userEmail) {
        return orderRepository.findByUserEmail(userEmail);
    }

    @Override
    public List<Order> findOrdersByUser(String userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public Order updateOrderStatus(String id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(status);

        // Update stock when order is delivered
        if (status == Order.OrderStatus.DELIVERED) {
            updateStockAfterOrder(order);
        }

        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

    @Override
    public BigDecimal calculateOrderTotal(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Order addItemToOrder(String orderId, OrderItem item) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!isStockAvailable(item.getMedicine().getId(), item.getQuantity())) {
            throw new IllegalArgumentException("Insufficient stock for medicine: " + item.getMedicine().getName());
        }

        order.getItems().add(orderItemRepository.save(item));
        order.setTotalAmount(calculateOrderTotal(order.getItems().stream().toList()));
        return orderRepository.save(order);
    }

    @Override
    public Order removeItemFromOrder(String orderId, String itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderItem itemToRemove = order.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Order item not found"));

        order.getItems().remove(itemToRemove);
        orderItemRepository.delete(itemToRemove);
        order.setTotalAmount(calculateOrderTotal(order.getItems().stream().toList()));
        return orderRepository.save(order);
    }

    @Override
    public boolean isStockAvailable(String medicineId, int quantity) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("Medicine not found"));
        return medicine.getQuantityInStock() >= quantity;
    }

    @Override
    public void updateStockAfterOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            Medicine medicine = item.getMedicine();
            medicine.setQuantityInStock(medicine.getQuantityInStock() - item.getQuantity());
            medicineRepository.save(medicine);
        }
    }

    @Override
    public Order getOrderById(String id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));
    }

    @Override
    public List<Order> getOrdersByUserEmail(String userEmail) {
        return findOrdersByUserEmail(userEmail);
    }

    @Override
    public boolean isOrderOwner(String orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return order.getUserEmail().equals(userEmail);
    }

}
