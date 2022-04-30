package com.motionbridge.motionbridge.order.application.helper;

import com.motionbridge.motionbridge.order.entity.Discount;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;

import java.math.BigDecimal;
import java.util.List;

import static com.motionbridge.motionbridge.commons.PriceCalculator.percentage;
import static com.motionbridge.motionbridge.commons.PriceCalculator.sum;

public class OrderPriceCalculator {

    public static BigDecimal recalculateOrderPriceAfterDiscountAppliedToOrder(BigDecimal base, BigDecimal pct) {
        return base.subtract(percentage(base, pct));
    }

    public static Order recalculateOrderPricesAfeterAddDiscountToSubscription(Order order, List<Subscription> subscriptions, Discount discount) {
        Order o;
        BigDecimal currentOrderPrice = new BigDecimal("00.00");
        BigDecimal totalOrderPrice = new BigDecimal("00.00");
        if (!subscriptions.isEmpty()) {
            for (Subscription subscription : subscriptions) {
                currentOrderPrice = sum(currentOrderPrice, subscription.getCurrentPrice());
                totalOrderPrice = sum(totalOrderPrice, subscription.getPrice());
            }
            order.setCurrentPrice(currentOrderPrice);
            order.setTotalPrice(totalOrderPrice);
            order.setActiveDiscount(true);
            order.setDiscountId(discount.getId());
            o = order;
        } else
            throw new NullPointerException("No subscriptions in order: " + order.getId());
        return o;
    }

    public static Order recalculateOrderPriceAndSave(Order order, SubscriptionUseCase.CreateSubscriptionCommand command){
        Order o;
        BigDecimal orderCurrentPrice = sum(order.getCurrentPrice(), command.getCurrentPrice());
        BigDecimal orderTotalPrice = sum(order.getTotalPrice(), command.getCurrentPrice());
        order.setCurrentPrice(orderCurrentPrice);
        order.setTotalPrice(orderTotalPrice);
        o = order;
        return o;
    }
}
