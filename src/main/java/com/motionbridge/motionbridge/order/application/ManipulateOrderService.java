package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.RestRichOrder.RestOrder;
import com.motionbridge.motionbridge.order.application.RestRichOrder.RestSubscription;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.db.OrderRepository;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ManipulateOrderService implements ManipulateOrderUseCase {
    final OrderRepository orderRepository;

    final SubscriptionUseCase subscription;

    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public Optional<Order> findByUserIdAndStatus(Long userId, Order.Status status) {
        return orderRepository.findOrderByUserIdAndOrderStatus(userId, status);
    }

    @Override
    public RestRichOrder findAllOrdersWithSubscriptions(Long userId) {
        return RestRichOrder.builder()
                .restOrders(toRestOrdersList(userId))
                .build();
    }

    public List<Order> getAllOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }

    private List<RestOrder> toRestOrdersList(Long userId) {
        List<RestOrder> restOrders = new ArrayList<>(Collections.emptyList());
        for (Order order : getAllOrdersByUserId(userId)) {
            restOrders.add(toRestOrder(order));
        }
        return restOrders;
    }

    private RestOrder toRestOrder(Order order) {
        return RestOrder.builder()
                .currentPrice(order.getCurrentPrice())
                .status(order.getStatus())
                .subscriptions(toRestSubsciptionsList(order.getId()))
                .build();
    }

    private List<RestSubscription> toRestSubsciptionsList(Long orderId) {
        List<RestSubscription> restSubscriptions = new ArrayList<>(Collections.emptyList());

        for (Subscription subscription : subscription.findAllByOrderId(orderId)) {
            restSubscriptions
                    .add(toRestSubscription(subscription));
        }

        return restSubscriptions;
    }

    private RestSubscription toRestSubscription(Subscription subscription) {
        return RestSubscription.builder()
                .currentPrice(subscription.getCurrentPrice())
                .animationsLimit(subscription.getAnimationsLimit())
                .type(subscription.getType())
                .timePeriod(subscription.getTimePeriod())
                .build();
    }


}


