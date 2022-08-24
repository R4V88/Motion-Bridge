package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductServiceTest {

    @Autowired
    ManipulateProductUseCase productUseCase;

    @Test
    void shouldReturnSuccesOnProductAdd() {
        //GIVEN
        ManipulateProductUseCase.CreateProductCommand productCommand
                = new ManipulateProductUseCase.CreateProductCommand(20, "instagram", "usd", "YEAR", new BigDecimal("120.0"));

        //WHEN
        final ManipulateProductUseCase.AddProductResponse addProductResponse = productUseCase.addProduct(productCommand);

        //THEN
        assertTrue(addProductResponse.isSuccess());
    }
}