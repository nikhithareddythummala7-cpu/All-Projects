package com.example.floweandgift.controller;

import com.example.floweandgift.model.Order;
import com.example.floweandgift.model.Product;
import com.example.floweandgift.model.Review;
import com.example.floweandgift.model.User;
import com.example.floweandgift.service.AdminActivityLogService;
import com.example.floweandgift.service.OrderService;
import com.example.floweandgift.service.ProductService;
import com.example.floweandgift.service.ReviewService;
import com.example.floweandgift.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminActivityLogService adminActivityLogService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {

        List<Order> allOrders = orderService.getAllOrders();
        List<Order> recentOrders = allOrders.stream().limit(10).toList();
        long totalOrders = allOrders.size();
        double totalSales = allOrders.stream().mapToDouble(Order::getTotalAmount).sum();

        List<Product> lowStockProducts = productService.getAllProducts().stream()
                .filter(p -> p.getStock() <= 5)
                .collect(Collectors.toList());

        List<Product> topSellers = productService.getAllProducts().stream()
                .sorted((p1, p2) -> Integer.compare(
                        allOrders.stream().mapToInt(o -> o.getItems().stream()
                                .filter(i -> i.getProductId().equals(p1.getId()))
                                .mapToInt(i -> i.getQuantity()).sum()).sum(),
                        allOrders.stream().mapToInt(o -> o.getItems().stream()
                                .filter(i -> i.getProductId().equals(p2.getId()))
                                .mapToInt(i -> i.getQuantity()).sum()).sum()))
                .limit(5)
                .collect(Collectors.toList());

        var recentActivities = adminActivityLogService.getRecentActivities()
                .stream().limit(10).collect(Collectors.toList());

        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("topSellers", topSellers);
        model.addAttribute("recentActivities", recentActivities);

        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String manageProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("product", new Product());
        return "admin/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable String id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("product", product.get());
            return "admin/products";
        } else {
            return "redirect:/admin/products";
        }
    }
    @GetMapping("/products/{id}/delete")
public String deleteProduct(@PathVariable String id, RedirectAttributes redirectAttributes) {

    productService.deleteProduct(id);

    redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
    return "redirect:/admin/products";
}


    // ✅ UPDATED — IMAGE URL ONLY (NO FILE UPLOAD)
    @PostMapping("/products")
    public String addOrUpdateProduct(
            @ModelAttribute Product product,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            RedirectAttributes redirectAttributes) {

        if (imageUrl != null && !imageUrl.isBlank()) {
            product.setImages(List.of(imageUrl));
        }

        if (product.getId() != null) {
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("message", "Product updated successfully!");
        } else {
            productService.addProduct(product);
            redirectAttributes.addFlashAttribute("message", "Product added successfully!");
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable String id,
            @RequestParam Order.OrderStatus status,
            RedirectAttributes redirectAttributes) {

        Order updatedOrder = orderService.updateOrderStatus(id, status);

        if (updatedOrder != null) {
            adminActivityLogService.logActivity("admin",
                    "Order Status Update",
                    "Updated order " + id + " to status " + status);
            redirectAttributes.addFlashAttribute("message", "Order status updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update order status");
        }

        return "redirect:/admin/orders";
    }

    @GetMapping("/reviews")
    public String manageReviews(Model model) {
        model.addAttribute("pendingReviews", reviewService.getPendingReviews());
        return "admin/reviews";
    }

    @PostMapping("/reviews/{id}/approve")
    public String approveReview(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<Review> approvedReview = reviewService.approveReview(id);
        if (approvedReview.isPresent()) {
            adminActivityLogService.logActivity("admin", "Review Approval", "Approved review " + id);
            redirectAttributes.addFlashAttribute("message", "Review approved successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to approve review");
        }
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable String id, RedirectAttributes redirectAttributes) {
        boolean deleted = reviewService.deleteReview(id);
        if (deleted) {
            adminActivityLogService.logActivity("admin", "Review Deletion", "Deleted review " + id);
            redirectAttributes.addFlashAttribute("message", "Review deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete review");
        }
        return "redirect:/admin/reviews";
    }

    // ===== USER MANAGEMENT ENDPOINTS =====

    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();

        // Calculate statistics
        long totalUsers = users.size();
        long activeUsers = users.stream().filter(User::isActive).count();
        long adminUsers = users.stream().filter(u -> u.getRole() == User.Role.ADMIN).count();
        long lockedUsers = users.stream().filter(User::isAccountLocked).count();

        model.addAttribute("users", users);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("adminUsers", adminUsers);
        model.addAttribute("lockedUsers", lockedUsers);

        return "admin/users";
    }

    @PostMapping("/users/{id}/verify")
    public String verifyUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setActive(true);
            userService.updateUser(user);
            adminActivityLogService.logActivity("admin", "User Verification", "Verified user " + user.getUsername());
            redirectAttributes.addFlashAttribute("message", "User verified successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-lock")
    public String toggleUserLock(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAccountLocked(!user.isAccountLocked());
            userService.updateUser(user);
            String action = user.isAccountLocked() ? "locked" : "unlocked";
            adminActivityLogService.logActivity("admin", "Account Lock Toggle", "User account " + action + " - " + user.getUsername());
            redirectAttributes.addFlashAttribute("message", "User account " + action + " successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.getUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String username = user.getUsername();
            userService.deleteUser(id);
            adminActivityLogService.logActivity("admin", "User Deletion", "Deleted user " + username);
            redirectAttributes.addFlashAttribute("message", "User deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found");
        }
        return "redirect:/admin/users";
    }

}
