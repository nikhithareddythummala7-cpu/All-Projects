package com.example.medlypharma.service;

import com.example.medlypharma.model.Order;
import com.example.medlypharma.model.OrderItem;
import com.example.medlypharma.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendWelcomeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Welcome to Medly-Pharma!");
        message.setText("Dear " + user.getFullName() + ",\n\n" +
                "Welcome to Medly-Pharma! Your account has been successfully created.\n\n" +
                "You can now browse our medicines, upload prescriptions, and place orders.\n\n" +
                "Best regards,\nMedly-Pharma Team");

        mailSender.send(message);
    }

    @Override
    @Async
    public void sendOrderConfirmationEmail(Order order) {
        String subject = "Order Confirmation - " + order.getOrderNumber();
        StringBuilder content = new StringBuilder();
        content.append("Dear ").append(order.getUser().getFullName()).append(",\n\n");
        content.append("Thank you for your order! Here are the details:\n\n");
        content.append("Order Number: ").append(order.getOrderNumber()).append("\n");
        content.append("Order Date: ").append(order.getOrderDate()).append("\n");
        content.append("Shipping Address: ").append(order.getShippingAddress()).append("\n\n");

        content.append("Items:\n");
        for (OrderItem item : order.getItems()) {
            content.append("- ").append(item.getMedicine().getName())
                   .append(" (Qty: ").append(item.getQuantity())
                   .append(", Price: $").append(item.getUnitPrice())
                   .append(", Total: $").append(item.getTotalPrice()).append(")\n");
        }

        content.append("\nTotal Amount: $").append(order.getTotalAmount()).append("\n\n");
        content.append("We will process your order shortly. You will receive updates on your order status.\n\n");
        content.append("Best regards,\nMedly-Pharma Team");

        sendEmail(order.getUser().getEmail(), subject, content.toString());
    }

    @Override
    @Async
    public void sendOrderStatusUpdateEmail(Order order) {
        String subject = "Order Status Update - " + order.getOrderNumber();
        String content = "Dear " + order.getUser().getFullName() + ",\n\n" +
                "Your order status has been updated.\n\n" +
                "Order Number: " + order.getOrderNumber() + "\n" +
                "New Status: " + order.getStatus() + "\n\n" +
                "Thank you for choosing Medly-Pharma!\n\n" +
                "Best regards,\nMedly-Pharma Team";

        sendEmail(order.getUser().getEmail(), subject, content);
    }

    @Override
    @Async
    public void sendLowStockAlert(String medicineName, int currentStock) {
        // This would typically be sent to admin emails
        String adminEmail = "admin@medlypharma.com"; // Configure this in properties
        String subject = "Low Stock Alert - " + medicineName;
        String content = "Alert: " + medicineName + " is running low on stock.\n\n" +
                "Current Stock: " + currentStock + "\n" +
                "Please restock soon.\n\n" +
                "Medly-Pharma System";

        sendEmail(adminEmail, subject, content);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
