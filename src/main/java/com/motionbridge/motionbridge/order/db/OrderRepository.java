package com.motionbridge.motionbridge.order.db;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    List<Order> findOrdersByUserIdAndOrderStatus(Long userId, OrderStatus status);

    List<Order> findAllByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM Order o WHERE o.id = :id")
    void deleteById(@NonNull Long id);

}
