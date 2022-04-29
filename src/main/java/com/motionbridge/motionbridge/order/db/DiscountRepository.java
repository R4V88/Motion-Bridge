package com.motionbridge.motionbridge.order.db;

import com.motionbridge.motionbridge.order.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findAllByCode(String code);
}
