package com.example.floweandgift.service;

import com.example.floweandgift.model.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private static final LocalTime SAME_DAY_CUTOFF = LocalTime.of(14, 0); // 2 PM cutoff for same-day delivery

    public boolean isSameDayDeliveryAvailable() {
        return LocalTime.now().isBefore(SAME_DAY_CUTOFF);
    }

    public List<Order.DeliveryTimeSlot> getAvailableTimeSlots() {
        LocalTime now = LocalTime.now();
        return List.of(Order.DeliveryTimeSlot.values()).stream()
                .filter(slot -> isSlotAvailable(slot, now))
                .collect(Collectors.toList());
    }

    private boolean isSlotAvailable(Order.DeliveryTimeSlot slot, LocalTime now) {
        switch (slot) {
            case MORNING_9_12:
                return now.isBefore(LocalTime.of(9, 0));
            case AFTERNOON_12_3:
                return now.isBefore(LocalTime.of(12, 0));
            case EVENING_3_6:
                return now.isBefore(LocalTime.of(15, 0));
            case NIGHT_6_9:
                return now.isBefore(LocalTime.of(18, 0));
            default:
                return false;
        }
    }

    public LocalDateTime calculateDeliveryDateTime(Order.DeliveryTimeSlot slot, boolean sameDay) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate deliveryDate = sameDay && isSameDayDeliveryAvailable() ? now.toLocalDate() : now.toLocalDate().plusDays(1);

        LocalTime deliveryTime;
        switch (slot) {
            case MORNING_9_12:
                deliveryTime = LocalTime.of(9, 0);
                break;
            case AFTERNOON_12_3:
                deliveryTime = LocalTime.of(12, 0);
                break;
            case EVENING_3_6:
                deliveryTime = LocalTime.of(15, 0);
                break;
            case NIGHT_6_9:
                deliveryTime = LocalTime.of(18, 0);
                break;
            default:
                deliveryTime = LocalTime.of(12, 0);
        }

        return LocalDateTime.of(deliveryDate, deliveryTime);
    }

    public boolean validateDeliveryDateTime(LocalDateTime deliveryDateTime) {
        LocalDateTime now = LocalDateTime.now();
        return deliveryDateTime.isAfter(now.plusHours(2)); // Minimum 2 hours advance booking
    }

    public LocalDate getDeliveryDate(Order.DeliveryTimeSlot slot, boolean sameDay) {
        LocalDate now = LocalDate.now();
        return sameDay && isSameDayDeliveryAvailable() ? now : now.plusDays(1);
    }
}
