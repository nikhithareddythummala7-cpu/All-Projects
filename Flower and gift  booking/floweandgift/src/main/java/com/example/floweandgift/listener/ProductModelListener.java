package com.example.floweandgift.listener;

import com.example.floweandgift.model.Product;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductModelListener implements BeforeConvertCallback<Product> {

    @Override
    public Product onBeforeConvert(Product product, String collection) {
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(UUID.randomUUID().toString());
        }
        return product;
    }
}
