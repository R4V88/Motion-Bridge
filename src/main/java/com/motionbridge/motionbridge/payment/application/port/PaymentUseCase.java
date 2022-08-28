package com.motionbridge.motionbridge.payment.application.port;

import com.motionbridge.motionbridge.payment.web.mapper.RestPaidOrderResponse;

public interface PaymentUseCase {

    RestPaidOrderResponse pay(long orderId, String username);
}
