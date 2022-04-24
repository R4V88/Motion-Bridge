package com.motionbridge.motionbridge.order.db;

import com.motionbridge.motionbridge.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
