package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.helper.CalculatedOrderPrice;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveDiscountUseCase;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.motionbridge.motionbridge.order.application.helper.OrderPriceCalculator.recalculateOrderPriceAfterRemoveDiscount;

@Service
@AllArgsConstructor
public class RemoveDiscountService implements RemoveDiscountUseCase {
    ManipulateOrderUseCase manipulateOrderUseCase;
    SubscriptionUseCase subscriptionUseCase;

    @Override
    public void removeDiscountFromOrderByIdAndUserId(Long orderId) {
        Order order = manipulateOrderUseCase.getOrderById(orderId);
        List<Subscription> subscriptions = subscriptionUseCase.findAllByOrderId(orderId);
        CalculatedOrderPrice calculatedOrderPrice = recalculateOrderPriceAfterRemoveDiscount(order, subscriptions);
        manipulateOrderUseCase.save(calculatedOrderPrice.getOrder());
        for(Subscription subscription: calculatedOrderPrice.getSubscriptions()) {
            subscriptionUseCase.saveSubscription(subscription);
        }
    }
}
