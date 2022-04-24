package com.motionbridge.motionbridge.product.application.port;

import com.motionbridge.motionbridge.commons.Either;
import com.motionbridge.motionbridge.product.application.RestActiveProduct;
import com.motionbridge.motionbridge.product.application.RestProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;

public interface ManipulateProductUseCase {
    List<RestActiveProduct> getActiveProducts();
    AddProductResponse addProduct(CreateProductCommand command);
    List<RestProduct> getAllProducts();
    SwitchStatusResponse switchStatus(Long id);

    @Builder
    @Value
    @AllArgsConstructor
    class CreateProductCommand {
        Integer animationQuantity;
        String name;
        String currency;
        Integer timePeriod;
        BigDecimal price;
    }

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
}
