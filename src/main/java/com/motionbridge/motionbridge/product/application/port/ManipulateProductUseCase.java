package com.motionbridge.motionbridge.product.application.port;

import com.motionbridge.motionbridge.commons.Either;
import com.motionbridge.motionbridge.product.application.RestActiveProduct;
import com.motionbridge.motionbridge.product.application.RestProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

public interface ManipulateProductUseCase {
    List<RestActiveProduct> getActiveProducts();
    AddProductResponse addProduct(AddProductCommand command);
    List<RestProduct> getAllProducts();

    @Builder
    @Value
    @AllArgsConstructor
    class AddProductCommand {
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
}
