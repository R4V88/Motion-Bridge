package com.motionbridge.motionbridge.order.application.port;

public interface ManipulateOrderUseCase {
    void addProduktToOrder();

    void addDiscount();

    void deleteOrder(Long orderId);
}
