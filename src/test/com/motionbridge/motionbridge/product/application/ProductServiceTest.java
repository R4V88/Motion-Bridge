package com.motionbridge.motionbridge.product.application;

import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

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
                = new ManipulateProductUseCase.CreateProductCommand("INSTAGRAM", "instagram", "usd", "MONTH",30, new BigDecimal("100.00"),
                "black", List.of(), List.of());

        //WHEN
        final ManipulateProductUseCase.AddProductResponse addProductResponse = productUseCase.addProduct(productCommand);

        //THEN
        assertTrue(addProductResponse.isSuccess());
    }
}
