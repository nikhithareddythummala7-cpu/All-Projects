package com.example.floweandgift.service;

import com.example.floweandgift.model.Payment;
import com.example.floweandgift.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment processPayment(Payment payment) {
        logger.info("Processing payment for order: {}, amount: {}", payment.getOrderId(), payment.getAmount());

        try {
            // Simulate payment processing with verification
            boolean isVerified = verifyPayment(payment);
            if (!isVerified) {
                payment.setStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason("Payment verification failed");
                logger.error("Payment verification failed for order: {}", payment.getOrderId());
                return paymentRepository.save(payment);
            }

            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setTransactionId("TXN" + System.currentTimeMillis());

            Payment savedPayment = paymentRepository.save(payment);
            logger.info("Payment processed successfully - Transaction ID: {}", payment.getTransactionId());
            return savedPayment;

        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("Payment processing error: " + e.getMessage());
            logger.error("Payment processing failed for order: {} - Error: {}", payment.getOrderId(), e.getMessage());
            return paymentRepository.save(payment);
        }
    }

    public boolean refundPayment(String transactionId) {
        logger.info("Processing refund for transaction: {}", transactionId);

        Optional<Payment> paymentOpt = paymentRepository.findByTransactionId(transactionId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();

            if (payment.getStatus() == Payment.PaymentStatus.COMPLETED) {
                // Simulate refund processing
                payment.setStatus(Payment.PaymentStatus.REFUNDED);
                paymentRepository.save(payment);
                logger.info("Refund processed successfully for transaction: {}", transactionId);
                return true;
            } else {
                logger.warn("Cannot refund payment - Status: {}", payment.getStatus());
                return false;
            }
        } else {
            logger.error("Payment not found for transaction: {}", transactionId);
            return false;
        }
    }

    public Payment verifyPayment(String orderId) {
        logger.debug("Verifying payment for order: {}", orderId);
        Optional<Payment> payment = paymentRepository.findByOrderId(orderId);
        return payment.orElse(null);
    }

    private boolean verifyPayment(Payment payment) {
        // Simulate payment verification logic
        // In real implementation, this would integrate with payment gateway
        logger.debug("Verifying payment details for order: {}", payment.getOrderId());
        return payment.getAmount() > 0 && payment.getMethod() != null;
    }

    public void handlePaymentFailure(String orderId, String reason) {
        logger.error("Payment failure for order: {} - Reason: {}", orderId, reason);

        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(reason);
            paymentRepository.save(payment);

            // Trigger auto-refund if payment was completed
            if (payment.getTransactionId() != null) {
                logger.info("Triggering auto-refund for failed payment - Transaction: {}", payment.getTransactionId());
                refundPayment(payment.getTransactionId());
            }
        }
    }
}
