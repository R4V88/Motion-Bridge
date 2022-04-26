package com.motionbridge.motionbridge.subscription.db;

import com.motionbridge.motionbridge.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("SELECT s FROM Subscription s WHERE s.userEntity.id = :id")
    List<Subscription> findSubscriptionsByUserId(Long id);

    @Query("SELECT s FROM Subscription s WHERE s.userEntity.id = :userId AND s.order.id = :orderId")
    List<Subscription> findSubscriptionsByUserIdAndOrderId(Long userId, Long orderId);
}
