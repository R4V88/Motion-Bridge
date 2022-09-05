package com.motionbridge.motionbridge.users.web.mapper;

import com.motionbridge.motionbridge.subscription.entity.Currency;
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
    Long id;
    Boolean isActive;
    BigDecimal currentPrice;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String type;
    Integer animationsLimit;
    Integer animationsCounter;
    String timePeriod;
    Long productId;
    String title;
    Currency currency;

    public static RestSubscription toRestSubscription(Subscription subscription) {
        return RestSubscription.builder()
                .id(subscription.getId())
                .isActive(subscription.getIsActive())
                .currentPrice(subscription.getCurrentPrice())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .type(subscription.getType())
                .animationsLimit(subscription.getAnimationsLimit())
                .animationsCounter(subscription.getAnimationsLimitCounter())
                .timePeriod(subscription.getTimePeriod())
                .productId(subscription.getProductId())
                .title(subscription.getTitle())
                .currency(subscription.getCurrency())
                .build();
    }

    public static List<RestSubscription> toRestSubsciptionsList(List<Subscription> subscriptions) {
        List<RestSubscription> restSubscriptions = new ArrayList<>(Collections.emptyList());

        for (Subscription sub : subscriptions) {
            restSubscriptions.add(toRestSubscription(sub));
        }

        return restSubscriptions;
    }
}
