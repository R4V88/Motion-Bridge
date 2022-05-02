package com.motionbridge.motionbridge.subscription.application.port;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

public interface ManipulateSubscriptionUseCase {
    List<Subscription> findAllByUserIdAndOrderId(Long userId, Long orderId);

    List<Subscription> findAllByUserId(Long id);

    List<Subscription> findAllByOrderId(Long orderId);

    void save(CreateSubscriptionCommand command);

    void saveSubscription(Subscription subscription);

    void deleteByIdAndOrderId(Long orderId, Long subscriptionId);

    @Value
    @AllArgsConstructor
    class CreateSubscriptionCommand {
        BigDecimal price;
        BigDecimal currentPrice;
        Integer animationsLimit;
        String type;
        String timePeriod;
        UserEntity user;
        Order order;
    }
}
