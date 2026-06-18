package com.example.medlypharma.service;

import com.example.medlypharma.dto.CartResponseDTO;
import com.example.medlypharma.model.CartItem;
import com.example.medlypharma.model.Medicine;
import com.example.medlypharma.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MedicineService medicineService;
    private final UserService userService;

    @Transactional
    public CartResponseDTO addToCart(String userEmail, String medicineId, int quantity) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("User email is required");
        }
        if (medicineId == null || medicineId.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine ID is required");
        }
        if (quantity <= 0) {
            quantity = 1; // Default to 1 if invalid quantity
        }

        // Get user safely - throw custom exception if not found
        var user = userService.getUserByEmail(userEmail);
        if (user == null) {
            throw new IllegalStateException("User not found: " + userEmail);
        }
        String userId = user.getId();

        // Get medicine safely - throw custom exception if not found
        Medicine medicine;
        try {
            medicine = medicineService.getMedicineById(medicineId);
            if (medicine == null) {
                throw new IllegalArgumentException("Medicine not found with ID: " + medicineId);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error fetching medicine: " + e.getMessage());
        }
        
        // Stock validation
        if (medicine.getQuantityInStock() <= 0) {
            throw new IllegalStateException("Medicine is out of stock: " + medicine.getName());
        }
        
        if (quantity > medicine.getQuantityInStock()) {
            throw new IllegalArgumentException("Requested quantity (" + quantity + ") exceeds available stock. Available: " + medicine.getQuantityInStock());
        }

        // Use list-based lookup to avoid NonUniqueResultExceptions if duplicates exist
        List<CartItem> existingItems = cartRepository.findAllByUserIdAndMedicineId(userId, medicineId);
        if (existingItems != null && !existingItems.isEmpty()) {
            // Consolidate duplicates into the first item
            CartItem primary = existingItems.get(0);
            int sumQuantity = primary.getQuantity();
            for (int i = 1; i < existingItems.size(); i++) {
                CartItem duplicate = existingItems.get(i);
                sumQuantity += duplicate.getQuantity();
                // remove duplicate documents
                cartRepository.deleteById(duplicate.getId());
            }

            int newQuantity = sumQuantity + quantity;
            if (newQuantity > medicine.getQuantityInStock()) {
                throw new IllegalArgumentException("Total quantity would exceed available stock. Available: " + medicine.getQuantityInStock());
            }
            primary.setQuantity(newQuantity);
            cartRepository.save(primary);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setMedicineId(medicineId);
            newItem.setMedicineName(medicine.getName());
            newItem.setImageUrl(medicine.getImageUrl());
            newItem.setQuantity(quantity);
            newItem.setPrice(medicine.getPrice().doubleValue());
            
            cartRepository.save(newItem);
        }
        
        // Return complete cart data
        return getCart(userEmail);
    }

    public CartResponseDTO getCart(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            return new CartResponseDTO(List.of(), 0.0, 0.0, 0);
        }

        var user = userService.getUserByEmail(userEmail);
        if (user == null) {
            return new CartResponseDTO(List.of(), 0.0, 0.0, 0);
        }

        List<CartItem> items = cartRepository.findByUserId(user.getId());
        if (items == null) {
            items = List.of();
        }

        double totalAmount = items.stream()
                .mapToDouble(i -> i.getQuantity() * i.getPrice())
                .sum();

        // Tiered delivery cost policy:
        // - free delivery for orders >= 1000
        // - ₹25 for orders >= 500 and < 1000
        // - ₹50 for orders < 500 (if cart not empty)
        double deliveryCost;
        if (items.isEmpty()) {
            deliveryCost = 0.0;
        } else if (totalAmount >= 1000.0) {
            deliveryCost = 0.0;
        } else if (totalAmount >= 500.0) {
            deliveryCost = 25.0;
        } else {
            deliveryCost = 50.0;
        }

        int totalItems = items.stream().mapToInt(CartItem::getQuantity).sum();

        return new CartResponseDTO(items, totalAmount, deliveryCost, totalItems);
    }

    @Transactional
    public CartItem updateCartItemQuantity(String userEmail, String medicineId, int quantity) {
        String userId = userService.getUserByEmail(userEmail).getId();
        // Use list-based lookup to avoid non-unique result issues and consolidate duplicates
        List<CartItem> items = cartRepository.findAllByUserIdAndMedicineId(userId, medicineId);
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Cart item not found");
        }

        // Consolidate duplicates into primary
        CartItem primary = items.get(0);
        for (int i = 1; i < items.size(); i++) {
            CartItem dup = items.get(i);
            primary.setQuantity(primary.getQuantity() + dup.getQuantity());
            cartRepository.deleteById(dup.getId());
        }

        if (quantity <= 0) {
            cartRepository.deleteByUserIdAndMedicineId(userId, medicineId);
            return null;
        }

        primary.setQuantity(quantity);
        return cartRepository.save(primary);
    }

    @Transactional
    public void removeFromCart(String userEmail, String medicineId) {
        String userId = userService.getUserByEmail(userEmail).getId();
        cartRepository.deleteByUserIdAndMedicineId(userId, medicineId);
    }

    @Transactional
    public void clearCart(String userEmail) {
        String userId = userService.getUserByEmail(userEmail).getId();
        cartRepository.deleteByUserId(userId);
    }

    public int getCartItemCount(String userEmail) {
        String userId = userService.getUserByEmail(userEmail).getId();
        return cartRepository.countByUserId(userId);
    }

    public List<Map<String, Object>> getCartItemsByUser(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            return List.of();
        }

        var user = userService.getUserByEmail(userEmail);
        if (user == null) {
            return List.of();
        }

        List<CartItem> items = cartRepository.findByUserId(user.getId());
        if (items == null || items.isEmpty()) {
            return List.of();
        }

        return items.stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", item.getId());
                    map.put("medicineId", item.getMedicineId());
                    map.put("medicineName", item.getMedicineName());
                    map.put("imageUrl", item.getImageUrl() != null ? item.getImageUrl() : "/images/medicine-placeholder.jpg");
                    map.put("quantity", item.getQuantity());
                    map.put("price", item.getPrice());
                    map.put("total", item.getQuantity() * item.getPrice());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
