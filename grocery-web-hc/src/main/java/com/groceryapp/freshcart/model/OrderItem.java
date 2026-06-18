package com.groceryapp.freshcart.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

@Document(collection = "order_items")
public class OrderItem {

    @Id
    private String id;

    @DBRef
    @JsonIgnore
    private Order order;   // Reference to parent Order

    @DBRef
    private Product product; // Reference to Product

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;

    // Constructors
    public OrderItem() {}

    public OrderItem(Order order, Product product, Integer quantity, BigDecimal price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.price != null) {
            this.subtotal = this.price.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) {
        this.price = price;
        if (this.quantity != null) {
            this.subtotal = price.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
