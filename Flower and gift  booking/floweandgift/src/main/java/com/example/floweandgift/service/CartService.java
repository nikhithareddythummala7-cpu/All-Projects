package com.example.floweandgift.service;

import com.example.floweandgift.model.Cart;
import com.example.floweandgift.model.Product;
import com.example.floweandgift.repository.CartRepository;
import com.example.floweandgift.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    // ✅ FIXED: CLEAN SINGLE METHOD
    public Cart getCartByUserId(String userId) {
        logger.debug("Retrieving cart for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setItems(new ArrayList<>());
                    c.setTotalAmount(0.0);
                    return cartRepository.save(c);
                });

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        return cart;
    }

    // ADD ITEM
    public Cart addItemToCart(String userId, String productId, int quantity) {

        Cart cart = getCartByUserId(userId);

        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }

        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            Cart.CartItem item = new Cart.CartItem(
                    productId,
                    product.getName(),
                    quantity,
                    product.getPrice(),
                    product.getImages() != null && !product.getImages().isEmpty()
                        ? product.getImages().get(0)
                        : "/img/default.png"
            );

            cart.getItems().add(item);
            cart.setTotalAmount(cart.getTotalAmount() + (quantity * product.getPrice()));

            return cartRepository.save(cart);
        }

        return cart;
    }

    public Cart removeItemFromCart(String userId, String productId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    public Cart updateItemQuantity(String userId, String productId, int quantity) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().forEach(item -> {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
            }
        });
        recalculateTotal(cart);
        return cartRepository.save(cart);
    }

    public void clearCart(String userId) {
        Cart cart = getCartByUserId(userId);
        cartRepository.deleteById(cart.getId());
    }

    private void recalculateTotal(Cart cart) {
        cart.setTotalAmount(
                cart.getItems()
                        .stream()
                        .mapToDouble(i -> i.getQuantity() * i.getPrice())
                        .sum()
        );
    }
}
