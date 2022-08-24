package com.motionbridge.motionbridge.payment.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.payment.application.port.PaymentUseCase;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import org.springframework.stereotype.Service;

@Service
public class PaymentService implements PaymentUseCase {

    ManipulateSubscriptionUseCase manipulateSubscriptionUseCase;
    ManipulateOrderUseCase manipulateOrderUseCase;

    @Override
    public void pay(Long orderId) {

    }
}
