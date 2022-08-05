package com.motionbridge.motionbridge.order.application.port;

import com.motionbridge.motionbridge.security.user.UserEntityDetails;

public interface RemoveSubscriptionFromOrderUseCase {
    void deleteSubscriptionInOrderByIdAndSubscriptionId(Long orderId, Long subscriptionId, UserEntityDetails user);
}
