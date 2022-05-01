package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.db.OrderRepository;
import com.motionbridge.motionbridge.order.entity.Discount;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.order.entity.SubscriptionType;
import com.motionbridge.motionbridge.order.web.mapper.RestOrder;
import com.motionbridge.motionbridge.order.web.mapper.RestRichOrder;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    final SubscriptionUseCase subscriptionService;

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public RestRichOrder getAllOrdersWithSubscriptions(Long userId) {
        return RestRichOrder.builder()
                .restOrders(toRestOrdersList(userId))
                .build();
    }

    @Override
    public void deleteSubscriptionInOrderByIdAndSubscriptionId(Long orderId, Long subscriptionId){
//        if (subscriptionService.findAllByOrderId(orderId).size() == 1) {
//            subscriptionService.deleteByIdAndOrderId(orderId, subscriptionId);
//            deleteOrder(orderId);
//        } else {
//            Order order = orderRepository.getById(orderId);
//            if(order.getDiscountId() != null && order.getActiveDiscount()) {
//                Long id = order.getDiscountId();
//                Discount discount = manipulateDiscountService.getById();
//
//                if(discount.getSubscriptionType().equals(SubscriptionType.ALL)) {
//                    subscriptionService.deleteByIdAndOrderId(orderId, subscriptionId);
//                    // przeliczenie po usunieciu subskrypcji -> pobranie ceny z order.totalPrice - cene subskrypcji i ponowne naliczenie discount.
//                } else {
//
//                }
//
//
//            }
//
//            //sprawdzenie po discount id, jakiego rodzaju byl to discount -> subskrypcja czy order to else
//
//            //jak subskrypcja to If -> sprawdzenie czy discount jest na usuwana subskrypcje czy nie
//            subscriptionService.deleteByIdAndOrderId(orderId, subscriptionId);
//
//        }
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.getById(orderId);
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

    private List<RestOrder> toRestOrdersList(Long userId) {
        List<RestOrder> restOrders = new ArrayList<>(Collections.emptyList());
        for (Order order : getAllOrdersByUserId(userId)) {
            restOrders.add(toRestOrder(order, subscriptionService));
        }
        return restOrders;
    }

    private List<Order> getAllOrdersByUserId(Long userId) {
        return orderRepository.findAllByUserId(userId);
    }
}


