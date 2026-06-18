package com.groceryapp.freshcart.repository;

import com.groceryapp.freshcart.model.CartItem;
import com.groceryapp.freshcart.model.Product;
import com.groceryapp.freshcart.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends MongoRepository<CartItem, String> {

    // Get all cart items for a user
    List<CartItem> findByUserId(String userId);

    // Find a specific cart item by user and product
    Optional<CartItem> findByUserIdAndProductId(String userId, String productId);

    // Delete all cart items for a user
    void deleteByUserId(String userId);
}
