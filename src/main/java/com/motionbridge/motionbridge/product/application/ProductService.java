package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.db.ProductRepository;
import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.product.entity.Product.Currency;
import com.motionbridge.motionbridge.product.entity.Product.ProductName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductService implements ManipulateProductUseCase {

    final ProductRepository repository;

    @Override
    public List<RestActiveProduct> getActiveProducts() {
        return repository.getAllActiveProducts()
                .stream()
                .map(this::toActiveProduct)
                .collect(Collectors.toList());
    }

    @Override
    public AddProductResponse addProduct(AddProductCommand command) {
        Product product = Product.builder()
                .name(ProductName.valueOf(command.getName().toUpperCase()))
                .animationQuantity(command.getAnimationQuantity())
                .price(command.getPrice())
                .currency(Currency.valueOf(command.getCurrency().toUpperCase()))
                .timePeriod(command.getTimePeriod())
                .build();
        Product saveProduct = repository.save(product);
        return AddProductResponse.success(saveProduct.getId());
    }

    @Override
    public List<RestProduct> getAllProducts() {
        return repository
                .findAll()
                .stream()
                .map(this::toCompleteRestProduct)
                .collect(Collectors.toList());
    }

    private RestProduct toCompleteRestProduct(Product product) {
        return new RestProduct(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCurrency().toString().toLowerCase(),
                product.getTimePeriod(),
                product.getAnimationQuantity(),
                product.getIsActive()
        );

    }

    private RestActiveProduct toActiveProduct(Product product) {
        return new RestActiveProduct(
                product.getName(),
                product.getPrice(),
                product.getCurrency().toString().toLowerCase(),
                product.getAnimationQuantity(),
                product.getTimePeriod()
                );
    }
}
