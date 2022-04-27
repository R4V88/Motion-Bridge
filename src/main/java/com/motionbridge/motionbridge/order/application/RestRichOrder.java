package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class RestRichOrder {
    List<RestOrder> restOrders;

    @Value
    @Builder
    static class RestOrder {
        OrderStatus status;
        BigDecimal currentPrice;
        List<RestSubscription> subscriptions;
    }

    @Value
    @Builder
    static class RestSubscription {
        BigDecimal currentPrice;
        Integer animationsLimit;
        String type;
        String timePeriod;
    }
}
