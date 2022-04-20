package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.application.port.ProductUseCase;
import com.motionbridge.motionbridge.product.db.ProductRepository;
import com.motionbridge.motionbridge.product.entity.Product;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductService implements ProductUseCase {

    final ProductRepository product;

    public List<ActiveProduct> getActiveProducts() {
//        List<Product> activeProducts = new ArrayList<>(Collections.emptyList());
//        for (Product product : product.getProductsByIsActiveIsTrue()) {
//            if (product.getIsActive()) {
//                activeProducts.add(product);
//            }
//        }
//        return activeProducts;

        return product.getAllActiveProducts()
                .stream()
                .map(this::toActiveProduct)
                .collect(Collectors.toList());
    }

    private ActiveProduct toActiveProduct(Product product) {
        return new ActiveProduct(
                product.getName(),
                product.getPrice()
        );
    }
}
