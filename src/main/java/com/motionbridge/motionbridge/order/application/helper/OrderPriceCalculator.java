package com.motionbridge.motionbridge.order.application.helper;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.entity.Subscription;

import java.math.BigDecimal;
import java.util.List;

import static com.motionbridge.motionbridge.commons.PriceCalculator.sum;

public class OrderPriceCalculator {

    void calculateOrderPrices(Order order, List<Subscription> subscriptions) {
        if (!subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                order.setCurrentPrice(sum(order.getCurrentPrice(), subscription.getCurrentPrice()));
                order.setTotalPrice(sum(order.getTotalPrice(), subscription.getPrice()));
            }
        } else {
            order.setCurrentPrice(BigDecimal.valueOf(0L));
            order.setTotalPrice(BigDecimal.valueOf(0L));
        }
    }
}
