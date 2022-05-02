package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.helper.CalculatedOrderPriceDTO;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveDiscountUseCase;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.motionbridge.motionbridge.order.application.helper.OrderPriceCalculator.recalculateOrderPriceAfterRemoveDiscount;

@Service
@AllArgsConstructor
public class RemoveDiscountService implements RemoveDiscountUseCase {
    ManipulateOrderUseCase manipulateOrderUseCase;
    ManipulateSubscriptionUseCase manipulateSubscriptionUseCase;

    @Override
    public void removeDiscountFromOrderByOrderId(Long orderId) {
        Order order = manipulateOrderUseCase.getOrderById(orderId);
        List<Subscription> subscriptions = manipulateSubscriptionUseCase.findAllByOrderId(orderId);
        CalculatedOrderPriceDTO calculatedOrderPriceDTO = recalculateOrderPriceAfterRemoveDiscount(order, subscriptions);
        manipulateOrderUseCase.save(calculatedOrderPriceDTO.getOrder());
        for (Subscription subscription : calculatedOrderPriceDTO.getSubscriptions()) {
            manipulateSubscriptionUseCase.saveSubscription(subscription);
        }
    }
}
