package com.example.floweandgift.repository;

import com.example.floweandgift.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategoryId(String categoryId);
    List<Product> findByType(Product.ProductType type);
    List<Product> findByOccasion(String occasion);
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
    List<Product> findByNameContainingIgnoreCase(String name);
    Optional<Product> findBySlug(String slug);
}
