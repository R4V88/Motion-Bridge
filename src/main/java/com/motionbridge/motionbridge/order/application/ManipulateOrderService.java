package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.db.OrderRepository;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.order.web.mapper.RestOrder;
import com.motionbridge.motionbridge.order.web.mapper.RestRichOrder;
import com.motionbridge.motionbridge.security.user.UserEntityDetails;
import com.motionbridge.motionbridge.security.user.UserSecurity;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.motionbridge.motionbridge.order.web.mapper.RestOrder.toRestOrder;

@Service
@Slf4j
@AllArgsConstructor
public class ManipulateOrderService implements ManipulateOrderUseCase {
    final OrderRepository orderRepository;
    final ManipulateSubscriptionUseCase subscriptionService;
    final UserSecurity userSecurity;


    @Transactional
    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public RestRichOrder getAllOrdersWithSubscriptions(Long userId, UserEntityDetails user) {
        return RestRichOrder.builder()
                .restOrders(toRestOrdersList(userId, user))
                .build();
    }

    private List<RestOrder> toRestOrdersList(Long userId, UserEntityDetails user) {
        List<RestOrder> restOrders = new ArrayList<>(Collections.emptyList());
        for (Order order : getAllOrdersByUserId(userId)) {
            restOrders.add(getRestOrderByOrderId(order.getId(), user));
        }
        return restOrders;
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).get();
    }

    @Override
    public void deleteAllOrdersByUserId(Long id) {
        orderRepository.deleteAllByUserId(id);
    }

    @Override
    public Order getOrderWithStatusNewByUserId(Long userId) {
        Optional<Order> retrievedOrder = getOptionalOrderWithStatusNewByUserId(userId);
        Order order;
        if (retrievedOrder.isPresent()) {
            order = retrievedOrder.get();
        } else
            throw new NoSuchElementException("Order with status NEW for user with id: " + userId + " does not exist!");
        return order;
    }

    private Optional<Order> getOptionalOrderWithStatusNewByUserId(Long userId) {
        for (Order order : getOrdersByUserIdAndStatus(userId, OrderStatus.NEW)) {
            if (order != null) {
                return Optional.of(order);
            } else
                throw new NoSuchElementException("Order with status NEW for user with id: " + userId + " does not exist!");
        }
        return Optional.empty();
    }

    @Override
    public List<Order> getOrdersByUserIdAndStatus(Long userId, OrderStatus status) {
        return orderRepository.findOrdersByUserIdAndOrderStatus(userId, status);
    }

    @Override
    public RestOrder getRestOrderByOrderId(Long orderId, UserEntityDetails user) {
        List<Subscription> subscriptions = subscriptionService.findAllByOrderId(orderId);
        Order order = orderRepository.findById(orderId).get();
        if (userSecurity.isOwnerOrAdmin(order.getUser().getEmail(), user)) {
            return toRestOrder(order, subscriptions);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    private List<Order> getAllOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }
}


