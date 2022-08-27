package com.motionbridge.motionbridge.product.application.port;

import com.motionbridge.motionbridge.commons.Either;
import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.product.web.mapper.RestActiveProduct;
import com.motionbridge.motionbridge.product.web.mapper.RestProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public interface ManipulateProductUseCase {
    List<RestActiveProduct> getActiveProducts();

    List<RestProduct> getAllProducts();

    AddProductResponse addProduct(CreateProductCommand command);

    SwitchStatusResponse switchStatus(Long id);

    Optional<Product> getProductById(Long id);

    ProductOrder checkIfProductExistInOrderThenGet(Long productId);

    class AddProductResponse extends Either<String, Long> {
        public AddProductResponse(boolean success, String left, Long right) {
            super(success, left, right);
        }

        public static AddProductResponse success(Long orderId) {
            return new AddProductResponse(true, null, orderId);
        }

        public static AddProductResponse failure(String error) {
            return new AddProductResponse(false, error, null);
        }
    }

    @Value
    class SwitchStatusResponse {
        public static SwitchStatusResponse SUCCESS = new SwitchStatusResponse(true, emptyList());

        boolean success;
        List<String> errors;
    }

    @Builder
    @Value
    @AllArgsConstructor
    class CreateProductCommand {
        Integer animationQuantity;
        String title;
        String currency;
        String timePeriod;
        BigDecimal price;
        String background;
        List<CreatePresentation> presentations;
        List<CreateParameter> parameters;
    }

    @AllArgsConstructor
    @Value
    class CreateParameter {
        String image;
        String subtitle;
        String title;
        String content;
        Product product;
    }

    @AllArgsConstructor
    @Value
    class CreatePresentation {
        String title;
        String content;
        String preview;
        Product product;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ProductOrder {
        Long id;
        Integer animationQuantity;
        String name;
        String currency;
        String timePeriod;
        BigDecimal price;
        Boolean isActive;
    }
}
