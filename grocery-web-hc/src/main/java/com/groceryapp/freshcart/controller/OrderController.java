package com.groceryapp.freshcart.controller;

import com.groceryapp.freshcart.model.*;
import com.groceryapp.freshcart.service.OrderService;
import com.groceryapp.freshcart.service.CartService;
import com.groceryapp.freshcart.repository.UserRepository;
import com.groceryapp.freshcart.security.UserDetailsImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    // ----------------- USER ENDPOINTS -----------------
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody OrderRequest orderRequest,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                String errors = bindingResult.getFieldErrors().stream()
                        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                        .collect(Collectors.joining(", "));
                return ResponseEntity.badRequest().body(new ErrorResponse(errors));
            }
            // Fetch the complete user from database instead of creating a new one
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<OrderItem> items;
            if (orderRequest.getOrderItems() != null && !orderRequest.getOrderItems().isEmpty()) {
                // Use client-provided order items (each with productId + quantity)
                items = orderRequest.getOrderItems().stream().map(dto -> {
                    OrderItem oi = new OrderItem();
                    Product ref = new Product();
                    ref.setId(dto.getProductId());
                    oi.setProduct(ref);
                    oi.setQuantity(dto.getQuantity());
                    return oi;
                }).toList();
            } else {
                // Fallback: build order items from the user's cart
                List<CartItem> cartItems = cartService.getUserCart(user.getId());
                if (cartItems == null || cartItems.isEmpty()) {
                    return ResponseEntity.badRequest().body(new ErrorResponse("Cart is empty"));
                }
                items = cartItems.stream().map(ci -> {
                    OrderItem oi = new OrderItem();
                    Product p = ci.getProduct();
                    if (p == null || p.getId() == null) {
                        throw new RuntimeException("Product missing in cart item");
                    }
                    Product ref = new Product();
                    ref.setId(p.getId());
                    oi.setProduct(ref);
                    oi.setQuantity(ci.getQuantity());
                    return oi;
                }).toList();
            }

            // Service will compute prices and persist
            Order order = orderService.createOrder(
                    user,
                    items,
                    orderRequest.getShippingAddress(),
                    orderRequest.getPaymentMethod(),
                    orderRequest.getPhoneNumber()
            );

            // Clear the user's cart after successful order creation
            cartService.clearUserCart(user.getId());

            return ResponseEntity.ok(OrderResponse.from(order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Failed to create order: " + e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Order> orders = orderService.getUserOrders(user);
        List<OrderResponse> resp = orders.stream().map(OrderResponse::from).toList();
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserOrderById(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String id) {  // Changed Long → String
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Order> order = orderService.getUserOrderById(id, user);

        return order.map(o -> ResponseEntity.ok(OrderResponse.from(o)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelOrder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String id) {  // Changed Long → String
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean cancelled = orderService.cancelOrder(id, user);
        if (cancelled) {
            return ResponseEntity.ok(new MessageResponse("Order cancelled successfully"));
        } else {
            return ResponseEntity.badRequest().body(new ErrorResponse("Cannot cancel order"));
        }
    }

    // ----------------- ADMIN ENDPOINTS -----------------
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String id,  // Changed Long → String
            @RequestBody StatusUpdateRequest statusUpdate) {
        Order order = orderService.updateOrderStatus(id, statusUpdate.getStatus());
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ----------------- DTO CLASSES -----------------
    public static class OrderResponse {
        private String id;
        private String userId;
        private List<OrderItemResponse> orderItems;
        private java.math.BigDecimal totalAmount;
        private OrderStatus status;
        private String shippingAddress;
        private String paymentMethod;
        private String phoneNumber;
        private java.time.LocalDateTime orderDate;
        private java.time.LocalDateTime deliveryDate;

        public static OrderResponse from(Order o) {
            OrderResponse r = new OrderResponse();
            r.id = o.getId();
            r.userId = o.getUser() != null ? o.getUser().getId() : null;
            r.totalAmount = o.getTotalAmount();
            r.status = o.getStatus();
            r.shippingAddress = o.getShippingAddress();
            r.paymentMethod = o.getPaymentMethod();
            r.phoneNumber = o.getPhoneNumber();
            r.orderDate = o.getOrderDate();
            r.deliveryDate = o.getDeliveryDate();
            r.orderItems = (o.getOrderItems() == null) ? java.util.List.of() :
                    o.getOrderItems().stream().map(OrderItemResponse::from).toList();
            return r;
        }

        public String getId() { return id; }
        public String getUserId() { return userId; }
        public List<OrderItemResponse> getOrderItems() { return orderItems; }
        public java.math.BigDecimal getTotalAmount() { return totalAmount; }
        public OrderStatus getStatus() { return status; }
        public String getShippingAddress() { return shippingAddress; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getPhoneNumber() { return phoneNumber; }
        public java.time.LocalDateTime getOrderDate() { return orderDate; }
        public java.time.LocalDateTime getDeliveryDate() { return deliveryDate; }
    }

    public static class OrderItemResponse {
        private String id;
        private String productId;
        private String name;
        private java.math.BigDecimal price;
        private Integer quantity;
        private java.math.BigDecimal subtotal;

        public static OrderItemResponse from(OrderItem i) {
            OrderItemResponse r = new OrderItemResponse();
            r.id = i.getId();
            r.productId = i.getProduct() != null ? i.getProduct().getId() : null;
            r.name = (i.getProduct() != null ? i.getProduct().getName() : null);
            r.price = i.getPrice();
            r.quantity = i.getQuantity();
            r.subtotal = i.getSubtotal();
            return r;
        }

        public String getId() { return id; }
        public String getProductId() { return productId; }
        public String getName() { return name; }
        public java.math.BigDecimal getPrice() { return price; }
        public Integer getQuantity() { return quantity; }
        public java.math.BigDecimal getSubtotal() { return subtotal; }
    }
    public static class OrderRequest {
        @Valid
        private List<OrderItemDto> orderItems;
        @NotBlank(message = "shippingAddress is required")
        private String shippingAddress;
        @NotBlank(message = "paymentMethod is required")
        private String paymentMethod;
        @NotBlank(message = "phoneNumber is required")
        private String phoneNumber;

        public List<OrderItemDto> getOrderItems() { return orderItems; }
        public void setOrderItems(List<OrderItemDto> orderItems) { this.orderItems = orderItems; }
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }

    public static class OrderItemDto {
        @NotBlank(message = "productId is required")
        private String productId;
        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "quantity must be at least 1")
        private Integer quantity;
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class StatusUpdateRequest {
        private OrderStatus status;
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
    }

    // ----------------- RESPONSE DTOS -----------------
    private static record ErrorResponse(String error) {}
    private static record MessageResponse(String message) {}
}
