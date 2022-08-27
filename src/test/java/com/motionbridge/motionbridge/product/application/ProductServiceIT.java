package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.db.ProductRepository;
import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.product.web.mapper.RestActiveProduct;
import com.motionbridge.motionbridge.product.web.mapper.RestProduct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductServiceIT {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ManipulateProductUseCase productUseCase;

    @Test
    void shouldReturnActiveProducts() {
        //GIVEN
        ManipulateProductUseCase.CreateProductCommand firstProductCommand = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "YEAR", new BigDecimal("120.00"));
        ManipulateProductUseCase.CreateProductCommand secondProductCommand = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "MONTH", new BigDecimal("90.00"));
        ManipulateProductUseCase.CreateProductCommand thirdProductCommand = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "YEAR", new BigDecimal("150.00"));
        ManipulateProductUseCase.CreateProductCommand fourthProductCommand = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "MONTH", new BigDecimal("100.00"));
        final ManipulateProductUseCase.AddProductResponse firstProductResponse = productUseCase.addProduct(firstProductCommand);
        final ManipulateProductUseCase.AddProductResponse secondProductResponse = productUseCase.addProduct(secondProductCommand);
        productUseCase.addProduct(thirdProductCommand);
        productUseCase.addProduct(fourthProductCommand);

        final Long firstId = firstProductResponse.getRight();
        final Long secondId = secondProductResponse.getRight();

        productUseCase.switchStatus(firstId);
        productUseCase.switchStatus(secondId);

        //WHEN
        final List<RestActiveProduct> activeProducts = productUseCase.getActiveProducts();

        //THEN
        assertEquals(2, activeProducts.size());
    }

    @Test
    void shouldReturnAllProducts() {
        //GIVEN
        ManipulateProductUseCase.CreateProductCommand firstProductCommand = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "YEAR", new BigDecimal("120.00"));
        ManipulateProductUseCase.CreateProductCommand secondProductCommand = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "MONTH", new BigDecimal("90.00"));
        ManipulateProductUseCase.CreateProductCommand thirdProductCommand = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "YEAR", new BigDecimal("150.00"));
        ManipulateProductUseCase.CreateProductCommand fourthProductCommand = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "MONTH", new BigDecimal("100.00"));
        productUseCase.addProduct(firstProductCommand);
        productUseCase.addProduct(secondProductCommand);
        productUseCase.addProduct(thirdProductCommand);
        productUseCase.addProduct(fourthProductCommand);

        //WHEN
        final List<RestProduct> allProducts = productUseCase.getAllProducts();
        //THEN
        assertEquals(4, allProducts.size());
    }

    @Test
    void shouldCheckIfProductExistInOrderThenReturn(){
        //GIVEN
        ManipulateProductUseCase.CreateProductCommand firstProductCommand = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "YEAR", new BigDecimal("120.00"));
        final ManipulateProductUseCase.AddProductResponse addProductResponse = productUseCase.addProduct(firstProductCommand);
        final Long productId = addProductResponse.getRight();

        //WHEN
        final ManipulateProductUseCase.ProductOrder productOrder = productUseCase.checkIfProductExistInOrderThenGet(productId);

        //THEN
        assertEquals(firstProductCommand.getName().toUpperCase(), productOrder.getName());
        assertEquals(firstProductCommand.getCurrency().toUpperCase(), productOrder.getCurrency());
        assertEquals(firstProductCommand.getAnimationQuantity(), productOrder.getAnimationQuantity());
        assertEquals(firstProductCommand.getPrice(), productOrder.getPrice());
        assertEquals(firstProductCommand.getTimePeriod().toUpperCase(), productOrder.getTimePeriod());
    }

    @Test
    void shouldSwitchProductStatus() {
        //GIVEN
        ManipulateProductUseCase.CreateProductCommand firstProductCommand = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "YEAR", new BigDecimal("120.00"));
        final ManipulateProductUseCase.AddProductResponse addProductResponse = productUseCase.addProduct(firstProductCommand);
        final Long productId = addProductResponse.getRight();
        final Optional<Product> productById = productUseCase.getProductById(productId);
        final Boolean isActiveBefore = productById.get().getIsActive();

        //WHEN
        productUseCase.switchStatus(productId);

        //THEN
        final Optional<Product> byId = productRepository.findById(productId);
        final Boolean isActiveAfter = byId.get().getIsActive();

        assertNotEquals(isActiveBefore, isActiveAfter);
    }
}
