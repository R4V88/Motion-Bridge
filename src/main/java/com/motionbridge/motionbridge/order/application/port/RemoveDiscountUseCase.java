package com.motionbridge.motionbridge.order.application.port;

import com.motionbridge.motionbridge.security.user.UserEntityDetails;

public interface RemoveDiscountUseCase {

    void removeDiscountFromOrderByOrderId(Long orderId, UserEntityDetails user);
}
