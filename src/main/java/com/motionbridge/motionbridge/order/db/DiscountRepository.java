package com.motionbridge.motionbridge.order.db;

import com.motionbridge.motionbridge.order.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
