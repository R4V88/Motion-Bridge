package com.motionbridge.motionbridge.order.application.port;

public interface RemoveSubscriptionFromOrderUseCase {
    void deleteSubscriptionInOrderByIdAndSubscriptionId(Long orderId, Long subscriptionId, String userEmail);
}
