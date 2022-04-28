package com.motionbridge.motionbridge.order.web.mapper;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.users.web.mapper.RestSubscription;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

import static com.motionbridge.motionbridge.users.web.mapper.RestSubscription.toRestSubsciptionsList;

@Value
@Builder
public class RestOrder {
    OrderStatus status;
    BigDecimal currentPrice;
    List<RestSubscription> subscriptions;

    public static RestOrder toRestOrder(Order order, SubscriptionUseCase subscription) {
        return RestOrder.builder()
                .currentPrice(order.getCurrentPrice())
                .status(order.getStatus())
                .subscriptions(toRestSubsciptionsList(order.getId(), subscription))
                .build();
    }
}
