package com.motionbridge.motionbridge.payment.application.port;

public interface PaymentUseCase {

    void pay(Long orderId);
}
