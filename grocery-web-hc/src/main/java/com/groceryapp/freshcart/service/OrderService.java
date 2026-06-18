package com.groceryapp.freshcart.service;

import com.groceryapp.freshcart.model.*;
import com.groceryapp.freshcart.repository.OrderRepository;
import com.groceryapp.freshcart.repository.OrderItemRepository;
import com.groceryapp.freshcart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrder(User user, List<OrderItem> orderItems, String shippingAddress, String paymentMethod, String phoneNumber) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        // Hydrate products and compute price/subtotals (do NOT save items yet)
        orderItems.forEach(item -> {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new RuntimeException("Invalid quantity for product: " + item.getProduct());
            }
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProduct().getId()));
            item.setProduct(product);
            BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
            item.setPrice(price);
            item.setSubtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        });

        // Calculate total amount
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // First create and save the Order WITHOUT attaching orderItems to avoid DBRef null id issues
        Order order = new Order(user, null, totalAmount, shippingAddress, paymentMethod);
        order.setPhoneNumber(phoneNumber);
        Order savedOrder = orderRepository.save(order);

        // Now set the order reference for each order item, save them, and update product stock
        orderItems.forEach(item -> {
            item.setOrder(savedOrder);
            orderItemRepository.save(item);

            // Update product stock if available
            Integer stock = item.getProduct().getStock();
            if (stock != null) {
                item.getProduct().setStock(Math.max(0, stock - item.getQuantity()));
                productRepository.save(item.getProduct());
            }
        });

        // Finally, attach saved items to the order and persist the relationship
        savedOrder.setOrderItems(orderItems);
        return orderRepository.save(savedOrder);
    }

    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId());
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByOrderDateDesc(status);
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> getUserOrderById(String id, User user) {
        return orderRepository.findByIdAndUserId(id, user.getId());
    }

    @Transactional
    public Order updateOrderStatus(String orderId, OrderStatus status) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            
            if (status == OrderStatus.DELIVERED) {
                order.setDeliveryDate(LocalDateTime.now());
            }
            
            return orderRepository.save(order);
        }
        return null;
    }

    @Transactional
    public boolean cancelOrder(String orderId, User user) {
        Optional<Order> orderOpt = orderRepository.findByIdAndUserId(orderId, user.getId());
        if (orderOpt.isPresent() && orderOpt.get().getStatus() == OrderStatus.PENDING) {
            Order order = orderOpt.get();
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            
            // Restore product stock
            order.getOrderItems().forEach(item -> {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            });
            
            return true;
        }
        return false;
    }
}
