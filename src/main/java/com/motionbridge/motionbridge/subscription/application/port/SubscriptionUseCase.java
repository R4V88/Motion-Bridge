package com.motionbridge.motionbridge.subscription.application.port;

import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

public interface SubscriptionUseCase {
    List<Subscription> findAllSubscriptionsByUserId(Long id);
    Subscription addSubscription(CreateSubscriptionCommand command);

    @Value
    @Builder
    class CreateSubscriptionCommand{
        BigDecimal price;
        BigDecimal currentPrice;
        Integer animationLimit;
        String type;
        UserEntity user;
    }
}
