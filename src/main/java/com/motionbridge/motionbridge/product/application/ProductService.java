package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.db.ProductRepository;
import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.product.web.mapper.RestActiveProduct;
import com.motionbridge.motionbridge.product.web.mapper.RestProduct;
import com.motionbridge.motionbridge.subscription.entity.Currency;
import com.motionbridge.motionbridge.subscription.entity.ProductName;
import com.motionbridge.motionbridge.subscription.entity.TimePeriod;
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
                .map(RestActiveProduct::toRestActiveProduct)
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
                .map(RestProduct::toRestProduct)
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

    @Override
    public ProductOrder checkIfProductExistInOrderThenGet(Long productId) {
        ProductOrder productOrder = new ProductOrder();

        if (getProductById(productId).isPresent()) {

            Product temp = getProductById(productId).get();

            productOrder = ProductOrder
                    .builder()
                    .id(temp.getId())
                    .price(temp.getPrice())
                    .currency(temp.getCurrency().toString())
                    .animationQuantity(temp.getAnimationQuantity())
                    .name(String.valueOf(temp.getName()).toUpperCase())
                    .timePeriod(String.valueOf(temp.getTimePeriod()).toUpperCase())
                    .isActive(temp.getIsActive())
                    .build();
            log.info("Product with Id: " + productId + " is accessible");
        } else {
            log.warn("Product with id: " + productId + " does not exist");
        }
        return productOrder;
    }

    private void switchActualStatus(Long id) {
        repository.getById(id).setIsActive(repository.getById(id).getIsActive() != null && !repository.getById(id).getIsActive());
    }
}
