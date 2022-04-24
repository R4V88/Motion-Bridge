package com.motionbridge.motionbridge.order.application.port;

import lombok.Value;

public interface ManipulateOrderUseCase {
    void  placeOrder(PlaceOrderCommand command);

    void addProduktToOrder();

    void addDiscount();

    void deleteOrder(Long orderId);


    @Value
    class PlaceOrderCommand {
        Long userId;
        Long productId;
    }
}
