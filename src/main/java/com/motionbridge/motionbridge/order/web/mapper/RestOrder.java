package com.motionbridge.motionbridge.order.web;

import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.subscription.web.RestSubscription;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class RestOrder {
    OrderStatus status;
    BigDecimal currentPrice;
    List<RestSubscription> subscriptions;
}
