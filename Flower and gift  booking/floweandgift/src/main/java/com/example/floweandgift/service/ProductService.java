package com.example.floweandgift.service;

import com.example.floweandgift.model.Product;
import com.example.floweandgift.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return products != null ? products : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error fetching all products: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductBySlug(String slug) {
        return productRepository.findBySlug(slug);
    }

    public List<Product> getProductsByCategory(String categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsByType(Product.ProductType type) {
        return productRepository.findByType(type);
    }

    public List<Product> getProductsByOccasion(String occasion) {
        return productRepository.findByOccasion(occasion);
    }

    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
