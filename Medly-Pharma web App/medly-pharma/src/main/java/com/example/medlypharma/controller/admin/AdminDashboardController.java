package com.example.medlypharma.controller.admin;

import com.example.medlypharma.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController extends BaseAdminController {
    
    private final DashboardService dashboardService;
    
    @GetMapping({"", "/dashboard"})
    public String dashboard(Model model) {
        try {
            // Add dashboard statistics
            model.addAttribute("totalOrders", dashboardService.getTotalOrders());
            model.addAttribute("totalRevenue", dashboardService.getTotalRevenue());
            model.addAttribute("totalProducts", dashboardService.getTotalProducts());
            model.addAttribute("totalCustomers", dashboardService.getTotalCustomers());
            
            // Add recent activities
            model.addAttribute("recentOrders", dashboardService.getRecentOrders(5));
            model.addAttribute("lowStockProducts", dashboardService.getLowStockProducts(5));
            
            return adminView("dashboard");
        } catch (Exception e) {
            // Log the full stack trace for debugging
            e.printStackTrace();
            // Add detailed error message to the model
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            // Add the exception to the model for detailed error page
            model.addAttribute("exception", e);
            return adminView("dashboard");
        }
    }
}