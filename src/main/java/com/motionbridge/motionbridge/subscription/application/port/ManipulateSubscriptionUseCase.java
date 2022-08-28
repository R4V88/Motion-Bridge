package com.motionbridge.motionbridge.subscription.application.port;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;

public interface ManipulateSubscriptionUseCase {
    List<Subscription> findAllByUserIdAndOrderId(Long userId, Long orderId);

    List<Subscription> findAllByUserId(Long id);

    List<Subscription> findAllByOrderId(Long orderId);

    List<Subscription> findAllByUserEmail(String email);

    void save(CreateSubscriptionCommand command);

    void saveSubscription(Subscription subscription);

    void deleteByIdAndOrderId(Long orderId, Long subscriptionId);

    void deleteAllByUserId(Long id);

    AutoRenewResponse autoRenew(Long id, String userEmail);

    void decrementAnimationsQuantity(Long id, String currentLoggedUsername);

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
        Long productId;
    }

    @Value
    class AutoRenewResponse {
        public static AutoRenewResponse SUCCESS = new AutoRenewResponse(true, emptyList());

        boolean success;
        List<String> errors;
    }
}
