package com.example.medlypharma.controller;

import com.example.medlypharma.dto.AddressDTO;
import com.example.medlypharma.dto.OrderDTO;
import com.example.medlypharma.dto.OrderItemDTO;
import com.example.medlypharma.model.Order;
import com.example.medlypharma.model.OrderItem;
import com.example.medlypharma.service.CartService;
import com.example.medlypharma.service.MedicineService;
import com.example.medlypharma.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final MedicineService medicineService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDTO>> getUserOrders(Authentication authentication) {
        String email = authentication.getName();
        List<Order> orders = orderService.getOrdersByUserEmail(email);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderService.isOrderOwner(#id, authentication.name)")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable String id) {
        Order order = orderService.getOrderById(id);
        OrderDTO orderDTO = convertToDTO(order);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody List<OrderItemDTO> orderItems, Authentication authentication) {
        String email = authentication.getName();
        List<OrderItem> items = orderItems.stream()
                .map(this::convertItemToEntity)
                .collect(Collectors.toList());
        Order order = orderService.createOrder(email, items);
        OrderDTO orderDTO = convertToDTO(order);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping("/place")
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody AddressDTO address, Authentication authentication) {
        String email = authentication.getName();

        // Get cart items for the user
        List<Map<String, Object>> cartItems = cartService.getCartItemsByUser(email);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Convert cart items to OrderItems
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem();
                    String medicineId = (String) item.get("medicineId");
                    orderItem.setMedicineId(medicineId);
                    orderItem.setMedicineName((String) item.get("medicineName"));
                    orderItem.setQuantity((Integer) item.get("quantity"));
                    orderItem.setUnitPrice(BigDecimal.valueOf((Double) item.get("price")));
                    // Fetch and set the Medicine entity
                    orderItem.setMedicine(medicineService.getMedicineById(medicineId));
                    // totalPrice is computed from unitPrice * quantity
                    return orderItem;
                })
                .collect(Collectors.toList());

        // Create order with address
        Order order = new Order();
        order.setUserEmail(email);
        order.setItems(new HashSet<>(orderItems));
        order.setShippingAddress(address.getFullName() + ", " + address.getAddressLine1() + ", " + address.getCity() + ", " + address.getState() + " - " + address.getPincode() + ", Phone: " + address.getPhone());
        order.setPaymentMethod(address.getPaymentMethod());
        order = orderService.createOrder(order); // Save with address

        // Clear cart after successful order
        cartService.clearCart(email);

        OrderDTO orderDTO = convertToDTO(order);
        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable String id, @RequestParam String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        Order updatedOrder = orderService.updateOrderStatus(id, orderStatus);
        OrderDTO orderDTO = convertToDTO(updatedOrder);
        return ResponseEntity.ok(orderDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUserFullName(order.getUser().getFullName());
        } else {
            dto.setUserId(null);
            dto.setUserFullName("Unknown");
        }
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setStatus(order.getStatus().name());
        dto.setItems(order.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList()));
        if (order.getPrescription() != null) {
            dto.setPrescriptionId(order.getPrescription().getId());
        }
        return dto;
    }

    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setMedicineId(item.getMedicine().getId());
        dto.setMedicineName(item.getMedicine().getName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    private OrderItem convertItemToEntity(OrderItemDTO dto) {
        OrderItem item = new OrderItem();
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        return item;
    }
}
