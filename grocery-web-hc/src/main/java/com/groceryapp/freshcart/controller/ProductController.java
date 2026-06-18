package com.groceryapp.freshcart.controller;

import com.groceryapp.freshcart.model.Product;
import com.groceryapp.freshcart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    // ----------------- GET ALL -----------------
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ----------------- GET BY ID -----------------
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {  // Changed Long → String
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------- GET BY CATEGORY -----------------
    @GetMapping("/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        return productRepository.findByCategory(category);
    }

    // ----------------- SEARCH BY NAME -----------------
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    // ----------------- CREATE PRODUCT -----------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    // ----------------- UPDATE PRODUCT -----------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setPrice(productDetails.getPrice());
                    product.setStock(productDetails.getStock());
                    product.setCategory(productDetails.getCategory());
                    product.setImageUrl(productDetails.getImageUrl());
                    product.setUnit(productDetails.getUnit());
                    product.setInStock(productDetails.isInStock());
                    Product updatedProduct = productRepository.save(product);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------- DELETE PRODUCT -----------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------- UPDATE STOCK -----------------
    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProductStock(@PathVariable String id, @RequestBody StockUpdateRequest stockUpdate) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setStock(stockUpdate.getStock());
                    product.setInStock(stockUpdate.getStock() > 0);
                    Product updatedProduct = productRepository.save(product);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------- UPDATE IN_STOCK -----------------
    @PatchMapping("/{id}/instock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProductInStock(@PathVariable String id, @RequestBody InStockUpdateRequest inStockUpdate) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setInStock(inStockUpdate.isInStock());
                    Product updatedProduct = productRepository.save(product);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------- DTOs -----------------
    public static class StockUpdateRequest {
        private Integer stock;
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
    }

    public static class InStockUpdateRequest {
        private boolean inStock;
        public boolean isInStock() { return inStock; }
        public void setInStock(boolean inStock) { this.inStock = inStock; }
    }
}
