package com.motionbridge.motionbridge.order.application.port;

import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.Value;

import java.math.BigDecimal;

public interface CreateOrderUseCase {

    void placeOrder(PlaceOrderCommand command);

    void saveOrder(CreateOrderCommand command);

    @Value
    class PlaceOrderCommand {
        Long userId;
        Long productId;

    }

    @Value
    class CreateOrderCommand {
        BigDecimal totalPrice;
        BigDecimal currentPrice;
        UserEntity user;

    }
}