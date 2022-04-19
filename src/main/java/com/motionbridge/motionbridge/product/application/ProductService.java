package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.application.port.ProductUseCase;
import com.motionbridge.motionbridge.product.db.ProductRepository;
import com.motionbridge.motionbridge.product.entity.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductService implements ProductUseCase {

    final ProductRepository product;

    public List<Product> getActiveProducts() {
        List<Product> activeProducts = new ArrayList<>(Collections.emptyList());
        for (Product product : product.getProductsByIsActiveIsTrue()) {
            if (product.getIsActive()) {
                activeProducts.add(product);
            }
        }
        return activeProducts;
    }
}
