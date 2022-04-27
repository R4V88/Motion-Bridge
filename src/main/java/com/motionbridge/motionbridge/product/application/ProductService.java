package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.db.ProductRepository;
import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.product.entity.Product.Currency;
import com.motionbridge.motionbridge.product.entity.Product.ProductName;
import com.motionbridge.motionbridge.product.entity.Product.TimePeriod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    @Transactional
    public AddProductResponse addProduct(CreateProductCommand command) {
        Product product = Product.builder()
                .name(ProductName.valueOf(command.getName().toUpperCase()))
                .animationQuantity(command.getAnimationQuantity())
                .price(command.getPrice())
                .currency(Currency.valueOf(command.getCurrency().toUpperCase()))
                .timePeriod(TimePeriod.valueOf(command.getTimePeriod().toUpperCase()))
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

    @Transactional
    @Override
    public SwitchStatusResponse switchStatus(Long id) {
        return repository.findById(id)
                .map(product -> {
                    switchActualStatus(id);
                    return SwitchStatusResponse.SUCCESS;
                })
                .orElseGet(() -> new SwitchStatusResponse(false, Collections.singletonList("Could not change status")));
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }

    private void switchActualStatus(Long id) {
        repository.getById(id).setIsActive(repository.getById(id).getIsActive() != null && !repository.getById(id).getIsActive());
    }

    private RestProduct toCompleteRestProduct(Product product) {
        return new RestProduct(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCurrency().toString().toLowerCase(),
                product.getAnimationQuantity(),
                product.getTimePeriod().toString(),
                product.getIsActive()
        );

    }

    private RestActiveProduct toActiveProduct(Product product) {
        return new RestActiveProduct(
                product.getName().toString(),
                product.getPrice(),
                product.getCurrency().toString().toLowerCase(),
                product.getAnimationQuantity(),
                product.getTimePeriod().toString()
                );
    }
}
