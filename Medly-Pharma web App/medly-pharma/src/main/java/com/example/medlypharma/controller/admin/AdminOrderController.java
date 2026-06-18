package com.example.medlypharma.controller.admin;

import com.example.medlypharma.model.Order;
import com.example.medlypharma.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String listOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        // Add status to model for the template to use
        model.addAttribute("status", status);

        // Get all orders first (we'll implement pagination later)
        List<Order> allOrders = orderService.findAllOrders();

        // Apply filters
        List<Order> filteredOrders = allOrders.stream()
            .filter(o -> keyword == null || keyword.isEmpty() ||
                    (o.getUserEmail() != null && o.getUserEmail().toLowerCase().contains(keyword.toLowerCase())) ||
                    (o.getId() != null && o.getId().toLowerCase().contains(keyword.toLowerCase())))
            .filter(o -> status == null || status.isEmpty() ||
                    (o.getStatus() != null && o.getStatus().name().equalsIgnoreCase(status)))
            .collect(Collectors.toList());

        // For now, we'll return all orders. In a real app, implement proper pagination
        model.addAttribute("orders", filteredOrders);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", 1);
        model.addAttribute("totalItems", filteredOrders.size());
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("pageNumbers", List.of(1));

        return "admin/orders/list";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable String id, Model model) {
        Order order = orderService.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        return "admin/orders/view";
    }

    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable String id, @RequestParam String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        orderService.updateOrderStatus(id, orderStatus);
        return "redirect:/admin/orders";
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return "redirect:/admin/orders";
    }
}
