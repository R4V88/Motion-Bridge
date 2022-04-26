package com.motionbridge.motionbridge.subscription.application.port;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

public interface SubscriptionUseCase {
    List<Subscription> findAllByUserIdAndOrderId(Long userId, Long orderId);

    List<Subscription> findAllByUserId(Long id);

    List<Subscription> findAllByOrderId(Long orderId);

    void save(CreateSubscriptionCommand command);

    void deleteByIdAndOrderId(Long orderId, Long subscriptionId);

    @Value
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
