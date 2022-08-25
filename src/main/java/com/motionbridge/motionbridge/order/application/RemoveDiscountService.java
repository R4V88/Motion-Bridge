package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.helper.CalculatedOrderPriceDTO;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveDiscountUseCase;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.motionbridge.motionbridge.order.application.helper.OrderPriceCalculator.recalculateOrderPriceAfterRemoveDiscount;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RemoveDiscountService implements RemoveDiscountUseCase {
    final ManipulateOrderUseCase manipulateOrderUseCase;
    final ManipulateSubscriptionUseCase manipulateSubscriptionUseCase;

    @Override
    public void removeDiscountFromOrderByOrderId(Long orderId, String userEmail) {
        Order order = manipulateOrderUseCase.getOrderById(orderId);
        if (order.getUser().getEmail().equals(userEmail)) {
            List<Subscription> subscriptions = manipulateSubscriptionUseCase.findAllByOrderId(orderId);
            CalculatedOrderPriceDTO calculatedOrderPriceDTO = recalculateOrderPriceAfterRemoveDiscount(order, subscriptions);
            manipulateOrderUseCase.save(calculatedOrderPriceDTO.getOrder());
            for (Subscription subscription : calculatedOrderPriceDTO.getSubscriptions()) {
                manipulateSubscriptionUseCase.saveSubscription(subscription);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
