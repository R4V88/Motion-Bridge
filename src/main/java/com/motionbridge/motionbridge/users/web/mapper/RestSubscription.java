package com.motionbridge.motionbridge.users.web.mapper;

import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Value
@Builder
public class RestSubscription {
    Boolean isActive;
    BigDecimal currentPrice;
    LocalDateTime endDate;
    String type;
    Integer animationsLimit;
    String timePeriod;

    public static RestSubscription toRestSubscription(Subscription subscription) {
        return RestSubscription.builder()
                .isActive(subscription.getIsActive())
                .currentPrice(subscription.getCurrentPrice())
                .endDate(subscription.getEndDate())
                .type(subscription.getType())
                .animationsLimit(subscription.getAnimationsLimit())
                .timePeriod(subscription.getTimePeriod())
                .build();
    }

    public static List<RestSubscription> toRestSubsciptionsList(Long orderId, SubscriptionUseCase subscription) {
        List<RestSubscription> restSubscriptions = new ArrayList<>(Collections.emptyList());

        for (Subscription sub : subscription.findAllByOrderId(orderId)) {
            restSubscriptions
                    .add(toRestSubscription(sub));
        }

        return restSubscriptions;
    }
}
