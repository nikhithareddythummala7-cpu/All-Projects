package com.example.medlypharma.controller.admin;

import com.example.medlypharma.model.Medicine;
import com.example.medlypharma.service.MedicineService;
import com.example.medlypharma.service.OrderService;
import com.example.medlypharma.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminReportsController {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy");

    private final OrderService orderService;
    private final UserService userService;
    private final MedicineService medicineService;

    @GetMapping
    public String reports(Model model) {
        try {
            log.info("Generating admin reports");
            
            // Get basic statistics using available service methods
            long totalOrders = orderService.countAllOrders();
            long totalUsers = userService.countAllUsers();
            long totalProducts = medicineService.countAllMedicines();

            // Get real data for the charts
            Map<String, Long> ordersByMonth = orderService.getOrdersByMonth();
            Map<String, Long> userRegistrationsByMonth = new LinkedHashMap<>();
            Map<String, Double> revenueByMonth = orderService.getRevenueByMonth();
            List<Medicine> topSellingProducts = medicineService.getTopSellingProducts(5);

            // Initialize user registrations data (placeholder for now)
            LocalDate now = LocalDate.now();
            for (int i = 11; i >= 0; i--) {
                YearMonth yearMonth = YearMonth.from(now.minusMonths(i));
                String monthKey = yearMonth.format(MONTH_FORMATTER);
                userRegistrationsByMonth.put(monthKey, 0L);
            }

            // Add attributes to the model
            model.addAttribute("totalOrders", totalOrders);
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalProducts", totalProducts);
            model.addAttribute("ordersByMonth", ordersByMonth);
            model.addAttribute("userRegistrationsByMonth", userRegistrationsByMonth);
            model.addAttribute("revenueByMonth", revenueByMonth);
            model.addAttribute("topSellingProducts", topSellingProducts);
            
            log.debug("Generated reports with {} orders, {} users, {} products", 
                    totalOrders, totalUsers, totalProducts);
                    
            return "admin/reports/index";
            
        } catch (Exception e) {
            log.error("Error generating reports", e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "An error occurred while generating reports. Please try again later.", 
                e
            );
        }
    }

    @GetMapping("/export")
    public String exportReports() {
        // Placeholder for report export functionality
        // In a real implementation, this would generate PDF/Excel reports
        return "redirect:/admin/reports";
    }
}
