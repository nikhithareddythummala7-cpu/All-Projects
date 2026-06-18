package com.groceryapp.freshcart.service;

import com.groceryapp.freshcart.model.CartItem;
import com.groceryapp.freshcart.model.Product;
import com.groceryapp.freshcart.model.User;
import com.groceryapp.freshcart.repository.CartItemRepository;
import com.groceryapp.freshcart.repository.ProductRepository;
import com.groceryapp.freshcart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CartItem> getUserCart(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional
    public CartItem addToCart(String userId, String productId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check stock availability (treat null as unlimited)
        int available = product.getStock() == null ? Integer.MAX_VALUE : product.getStock();
        if (available < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + available);
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(user.getId(), product.getId());
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;
            
            // Check if new quantity exceeds stock
            int available2 = product.getStock() == null ? Integer.MAX_VALUE : product.getStock();
            if (available2 < newQuantity) {
                throw new RuntimeException("Cannot add more than available stock. Available: " + available2);
            }
            
            item.setQuantity(newQuantity);
            return cartItemRepository.save(item);
        } else {
            CartItem cartItem = new CartItem(user, product, quantity);
            return cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public CartItem updateCartItemQuantity(String itemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        Product product = cartItem.getProduct();
        
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }
        
        // Check stock availability (treat null as unlimited)
        int available = product.getStock() == null ? Integer.MAX_VALUE : product.getStock();
        if (available < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + available);
        }
        
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeCartItem(String itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearUserCart(String userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        cartItemRepository.deleteByUserId(userId);
    }

    public double calculateCartTotal(String userId) {
        List<CartItem> cartItems = getUserCart(userId);
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice().doubleValue() * item.getQuantity())
                .sum();
    }

    public int getCartItemCount(String userId) {
        List<CartItem> cartItems = getUserCart(userId);
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
