package com.motionbridge.motionbridge.order.web.mapper;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.users.web.mapper.RestSubscription;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

import static com.motionbridge.motionbridge.users.web.mapper.RestSubscription.toRestSubsciptionsList;

@Value
@Builder
public class RestOrder {
    Long id;
    OrderStatus status;
    BigDecimal currentPrice;
    List<RestSubscription> subscriptions;

    public static RestOrder toRestOrder(Order order, List<Subscription> subscriptions) {
        return RestOrder.builder()
                .id(order.getId())
                .currentPrice(order.getCurrentPrice())
                .status(order.getStatus())
                .subscriptions(toRestSubsciptionsList(subscriptions))
                .build();
    }
}
