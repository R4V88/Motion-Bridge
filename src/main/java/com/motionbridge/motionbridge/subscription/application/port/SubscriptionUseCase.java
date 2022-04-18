package com.motionbridge.motionbridge.subscription.application.port;

import com.motionbridge.motionbridge.subscription.entity.Subscription;

import java.util.List;

public interface SubscriptionUseCase {
    List<Subscription> findAllSubscriptionsByUserId(Long id);
}
