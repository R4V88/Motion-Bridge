package com.motionbridge.motionbridge.payment.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.payment.application.port.PaymentUseCase;
import com.motionbridge.motionbridge.payment.web.mapper.RestPaidOrderResponse;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.subscription.entity.TimePeriod;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentService implements PaymentUseCase {

    ManipulateSubscriptionUseCase manipulateSubscriptionUseCase;
    ManipulateOrderUseCase manipulateOrderUseCase;

    @Override
    public RestPaidOrderResponse pay(long orderId, String username) {
//        final Long orderLongId = Long.valueOf(orderId);
        List<Long> subscriptionsIds = new ArrayList<>();
        final Order orderById = manipulateOrderUseCase.getOrderById(orderId);
        if (orderById.getUser().getEmail().equals(username)) {
            orderById.setIsLocked(true);
            orderById.setStatus(OrderStatus.PAID);
            manipulateOrderUseCase.save(orderById);
            final List<Subscription> allByOrderId = manipulateSubscriptionUseCase.findAllByOrderId(orderId);
            for (Subscription subscription : allByOrderId) {
                subscription.setIsActive(true);
                if (TimePeriod.MONTH.name().equals(subscription.getTimePeriod().toUpperCase())) {
                    subscription.setEndDate(LocalDateTime.now().plusDays(TimePeriod.MONTH.getPeriod()));
                } else if (TimePeriod.YEAR.name().equals(subscription.getTimePeriod().toUpperCase())) {
                    subscription.setEndDate(LocalDateTime.now().plusDays(TimePeriod.YEAR.getPeriod()));
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong Time Period");
                }
                manipulateSubscriptionUseCase.saveSubscription(subscription);
                subscriptionsIds.add(subscription.getId());
            }
            return new RestPaidOrderResponse(orderId, subscriptionsIds);

        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unathorized");
        }
    }
}
