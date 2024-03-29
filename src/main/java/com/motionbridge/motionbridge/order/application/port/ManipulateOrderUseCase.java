package com.motionbridge.motionbridge.order.application.port;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.order.web.mapper.RestOrder;
import com.motionbridge.motionbridge.order.web.mapper.RestRichOrder;

import java.util.List;

public interface ManipulateOrderUseCase {

    void save(Order order);

    void deleteOrder(Long orderId);

    RestRichOrder getAllOrdersWithSubscriptions(String user);

    List<Order> getOrdersByUserIdAndStatus(Long userId, OrderStatus status);

    Order getOrderWithStatusNewByUserId(Long userId);

    RestOrder getRestOrderByOrderId(Long orderId, String userEmail);

    Order getOrderById(Long orderId);

    void deleteAllOrdersByUserId(Long id);
}
