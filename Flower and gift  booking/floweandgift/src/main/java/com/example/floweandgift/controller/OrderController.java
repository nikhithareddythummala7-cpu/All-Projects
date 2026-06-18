package com.example.floweandgift.controller;

import com.example.floweandgift.model.Order;
import com.example.floweandgift.model.User;
import com.example.floweandgift.service.OrderService;
import com.example.floweandgift.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String orderHistory(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            if (userDetails == null) {
                return "redirect:/login";
            }
            String userId = getUserId(userDetails);
            if (userId == null) {
                System.out.println("OrderController: User not found in database");
                return "redirect:/login";
            }
            
            System.out.println("OrderController: Fetching orders for user: " + userId);
            List<Order> orders = orderService.getOrdersByUser(userId);
            
            if (orders == null) {
                orders = new ArrayList<>();
            }
            
            System.out.println("OrderController: Found " + orders.size() + " orders");
            
            // Ensure all orders have required fields to prevent null pointer issues in template
            orders.forEach(order -> {
                if (order.getStatus() == null) {
                    order.setStatus(Order.OrderStatus.PLACED);
                }
                if (order.getCreatedAt() == null) {
                    order.setCreatedAt(java.time.LocalDateTime.now());
                }
                if (order.getTotalAmount() == 0 && order.getItems() != null) {
                    order.setTotalAmount(order.getItems().stream()
                            .mapToDouble(item -> item.getQuantity() * item.getPrice())
                            .sum());
                }
            });
            
            model.addAttribute("orders", orders);
            return "orders/history";
        } catch (Exception e) {
            System.err.println("Error fetching order history: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Database connection error. Please try again later.");
            model.addAttribute("orders", new ArrayList<>());
            return "orders/history";
        }
    }

    @GetMapping("/{id}")
    public String orderDetails(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        String userId = getUserId(userDetails);
        if (userId == null) {
            return "redirect:/login";
        }
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isPresent() && order.get().getUserId().equals(userId)) {
            Order orderObj = order.get();
            // Ensure order has required fields
            if (orderObj.getStatus() == null) {
                orderObj.setStatus(Order.OrderStatus.PLACED);
            }
            if (orderObj.getCreatedAt() == null) {
                orderObj.setCreatedAt(java.time.LocalDateTime.now());
            }
            model.addAttribute("order", orderObj);
            return "orders/detail";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails, RedirectAttributes redirectAttributes) {
        String userId = getUserId(userDetails);
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Unable to cancel order");
            return "redirect:/login";
        }
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isPresent() && order.get().getUserId().equals(userId) && order.get().getStatus() == Order.OrderStatus.PLACED) {
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("message", "Order cancelled successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to cancel order");
        }
        return "redirect:/orders/" + id;
    }

    private String getUserId(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userService.findByUsername(userDetails.getUsername()).map(User::getId).orElse(null);
    }
}
