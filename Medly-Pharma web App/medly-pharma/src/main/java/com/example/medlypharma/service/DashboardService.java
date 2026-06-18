package com.example.medlypharma.service;

import com.example.medlypharma.model.Medicine;
import com.example.medlypharma.model.Order;
import com.example.medlypharma.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    
    private final OrderService orderService;
    private final UserService userService;
    private final MedicineService medicineService;
    
    public long getTotalOrders() {
        try {
            return orderService.countAllOrders();
        } catch (Exception e) {
            return 0L;
        }
    }
    
    public BigDecimal getTotalRevenue() {
        try {
            return orderService.calculateTotalRevenue();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
    
    public long getTotalProducts() {
        try {
            return medicineService.countAllMedicines();
        } catch (Exception e) {
            return 0L;
        }
    }
    
    public long getTotalCustomers() {
        try {
            return userService.countAllUsers();
        } catch (Exception e) {
            return 0L;
        }
    }
    
    public List<Order> getRecentOrders(int count) {
        try {
            return orderService.findRecentOrders(count);
        } catch (Exception e) {
            return List.of();
        }
    }
    
    public List<Medicine> getLowStockProducts(int count) {
        try {
            return medicineService.findLowStockMedicines(count);
        } catch (Exception e) {
            return List.of();
        }
    }
}
