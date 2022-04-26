package com.motionbridge.motionbridge.order.db;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.Order.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    Optional<Order> findOrderByUserIdAndOrderStatus(Long userId, Status status);

//    @Query("")
//    Long orderId saveOrderAndGetId(Order order);
}
