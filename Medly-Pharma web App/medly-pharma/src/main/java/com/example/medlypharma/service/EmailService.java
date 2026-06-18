package com.example.medlypharma.service;

import com.example.medlypharma.model.Order;
import com.example.medlypharma.model.User;

public interface EmailService {
    void sendWelcomeEmail(User user);
    void sendOrderConfirmationEmail(Order order);
    void sendOrderStatusUpdateEmail(Order order);
    void sendLowStockAlert(String medicineName, int currentStock);
}
