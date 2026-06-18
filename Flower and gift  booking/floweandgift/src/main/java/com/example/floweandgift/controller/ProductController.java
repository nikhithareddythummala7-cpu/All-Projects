package com.example.floweandgift.controller;

import com.example.floweandgift.model.Product;
import com.example.floweandgift.model.Review;
import com.example.floweandgift.service.ProductService;
import com.example.floweandgift.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public String listProducts(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "search", required = false) String search,
            Model model) {
        
        try {
            List<Product> products = productService.getAllProducts();
            
            // Apply search filter first
            if (search != null && !search.trim().isEmpty()) {
                products = productService.searchProducts(search);
            }
            
            // Apply type filter
            if (type != null && !type.trim().isEmpty()) {
                try {
                    Product.ProductType productType = Product.ProductType.valueOf(type.toUpperCase());
                    products = products.stream()
                            .filter(p -> p.getType() == productType)
                            .toList();
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid product type: " + type);
                }
            }
            
            // Apply price range filter
            if (minPrice != null && minPrice >= 0) {
                double max = (maxPrice != null && maxPrice >= minPrice) ? maxPrice : Double.MAX_VALUE;
                final double finalMax = max;
                products = products.stream()
                        .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= finalMax)
                        .toList();
            } else if (maxPrice != null && maxPrice >= 0) {
                final double max = maxPrice;
                products = products.stream()
                        .filter(p -> p.getPrice() <= max)
                        .toList();
            }
            
            model.addAttribute("products", products != null ? products : List.of());
            model.addAttribute("selectedType", type);
            model.addAttribute("selectedMinPrice", minPrice);
            model.addAttribute("selectedMaxPrice", maxPrice);
            model.addAttribute("selectedSearch", search);
            
            return "products/list";
        } catch (Exception e) {
            System.err.println("Error in listProducts: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("products", List.of());
            model.addAttribute("error", "An error occurred while loading products");
            return "products/list";
        }
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable String id, Model model) {
        try {
            Product product = productService.getProductById(id).orElse(null);
            
            if (product == null) {
                model.addAttribute("error", "Product not found");
                model.addAttribute("products", productService.getAllProducts());
                return "products/list";
            }
            
            List<Review> reviews = reviewService.getReviewsByProductId(id);
            if (reviews == null) {
                reviews = List.of();
            }
            
            model.addAttribute("product", product);
            model.addAttribute("reviews", reviews);
            model.addAttribute("newReview", new Review());
            return "products/detail";
        } catch (Exception e) {
            System.err.println("Error loading product detail: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "An error occurred while loading the product");
            model.addAttribute("products", productService.getAllProducts());
            return "products/list";
        }
    }

    @PostMapping("/{id}/reviews")
    public String addReview(@PathVariable String id, @ModelAttribute Review review, Principal principal, RedirectAttributes redirectAttributes) {
        review.setProductId(id);
        review.setUserId(principal.getName());
        reviewService.addReview(review);
        redirectAttributes.addFlashAttribute("message", "Review added successfully");
        return "redirect:/products/" + id;
    }
}
