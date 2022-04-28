package com.motionbridge.motionbridge.order.application.port;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.order.web.mapper.RestRichOrder;

import java.util.Optional;

public interface ManipulateOrderUseCase {

    void deleteOrder(Long orderId);

    Optional<Order> findOrderById(Long orderId);

    Optional<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    RestRichOrder findAllOrdersWithSubscriptions(Long userId);
}
