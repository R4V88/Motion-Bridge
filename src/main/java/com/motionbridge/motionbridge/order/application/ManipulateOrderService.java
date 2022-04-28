package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.db.OrderRepository;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.order.web.mapper.RestOrder;
import com.motionbridge.motionbridge.order.web.mapper.RestRichOrder;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.motionbridge.motionbridge.order.web.mapper.RestOrder.toRestOrder;

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
    public Optional<Order> findByUserIdAndStatus(Long userId, OrderStatus status) {
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
            restOrders.add(toRestOrder(order, subscription));
        }
        return restOrders;
    }
}


