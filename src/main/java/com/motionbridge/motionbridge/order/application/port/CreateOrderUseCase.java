package com.motionbridge.motionbridge.order.application.port;

import com.motionbridge.motionbridge.commons.Either;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.web.mapper.RestOrderId;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.Value;

import java.math.BigDecimal;

public interface CreateOrderUseCase {

    RestOrderId placeOrder(PlaceOrderCommand command, String currentlyLoggedUserEmail);

    Order saveOrder(CreateOrderCommand command);

    class CreateOrderResponse extends Either<String, Order> {

        public CreateOrderResponse(boolean success, String left, Order right) {
            super(success, left, right);
        }

        public static CreateOrderResponse success(Order right) {
            return new CreateOrderResponse(true, null, right);
        }

        public static CreateOrderResponse failure(String left) {
            return new CreateOrderResponse(false, left, null);
        }
    }

    @Value
    class PlaceOrderCommand {
        Long productId;
    }

    @Value
    class CreateOrderCommand {
        BigDecimal totalPrice;
        BigDecimal currentPrice;
        UserEntity user;
    }

}
