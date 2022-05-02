package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.helper.CalculatedOrderPriceDTO;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveSubscriptionFromOrderUseCase;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.motionbridge.motionbridge.order.application.helper.OrderPriceCalculator.recalculateOrderPrice;

@Slf4j
@Service
@AllArgsConstructor
public class RemoveSubscriptionFromOrderService implements RemoveSubscriptionFromOrderUseCase {

    final RemoveDiscountUseCase removeDiscountUseCase;
    final ApplyDiscountService applyDiscountService;
    final ManipulateDiscountUseCase manipulateDiscountUseCase;
    final ManipulateOrderUseCase manipulateOrderUseCase;
    final ManipulateSubscriptionUseCase manipulateSubscriptionUseCase;

    @Transactional
    @Override
    public void deleteSubscriptionInOrderByIdAndSubscriptionId(Long orderId, Long subscriptionId) {
        Order order = manipulateOrderUseCase.getOrderById(orderId);
        List<Subscription> subscriptions = manipulateSubscriptionUseCase.findAllByOrderId(orderId);

        if (subscriptions.size() == 1) {
            manipulateSubscriptionUseCase.deleteByIdAndOrderId(orderId, subscriptionId);
            log.info("Subscription with id: " + subscriptionId + " has been removed.");

            manipulateOrderUseCase.deleteOrder(orderId);
            log.info("Order with id: " + orderId + " hase been removed");
        } else {
            if (order.getActiveDiscount()) {
                Long discountId = order.getDiscountId();
                String code = manipulateDiscountUseCase.getDiscountById(discountId).getCode();
                removeDiscountUseCase.removeDiscountFromOrderByOrderId(orderId);
                log.info("Removed discount for order: " + orderId);

                manipulateSubscriptionUseCase.deleteByIdAndOrderId(orderId, subscriptionId);
                log.info("Removed subscription: " + subscriptionId + " for order: " + orderId);

                List<Subscription> subscriptionList = manipulateSubscriptionUseCase.findAllByOrderId(orderId);
                CalculatedOrderPriceDTO calculatedOrderPriceDTO = recalculateOrderPrice(order, subscriptionList);
                log.info("Recalculating prices for order: " + orderId);

                manipulateOrderUseCase.save(calculatedOrderPriceDTO.getOrder());
                for (Subscription subscription : calculatedOrderPriceDTO.getSubscriptions()) {
                    manipulateSubscriptionUseCase.saveSubscription(subscription);
                }
                log.info("Recalculated prices for order: " + orderId);

                applyDiscountService.getValidDiscountToOrder(code, order);
                log.info("Reapplied discount for order: " + orderId + "and recalculated prices");

            } else if (!order.getActiveDiscount()) {
                log.info("Order: " + orderId + " has no active discount");
                manipulateSubscriptionUseCase.deleteByIdAndOrderId(orderId, subscriptionId);
                log.info("Removed subscription: " + subscriptionId + " for order: " + orderId);

                List<Subscription> subscriptionList = manipulateSubscriptionUseCase.findAllByOrderId(orderId);
                CalculatedOrderPriceDTO calculatedOrderPriceDTO = recalculateOrderPrice(order, subscriptionList);
                log.info("Recalculating prices for order: " + orderId);
                manipulateOrderUseCase.save(calculatedOrderPriceDTO.getOrder());
                for (Subscription subscription : calculatedOrderPriceDTO.getSubscriptions()) {
                    manipulateSubscriptionUseCase.saveSubscription(subscription);
                }
                log.info("Recalculated prices for order: " + orderId);
            }
        }
    }
}
