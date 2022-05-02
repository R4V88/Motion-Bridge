package com.motionbridge.motionbridge.order.application.port;

public interface RemoveDiscountUseCase {

    void removeDiscountFromOrderByOrderId(Long orderId);
}
