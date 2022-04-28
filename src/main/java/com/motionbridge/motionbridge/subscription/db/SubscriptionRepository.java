package com.motionbridge.motionbridge.subscription.db;

import com.motionbridge.motionbridge.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("SELECT s FROM Subscription s WHERE s.user.id = :id")
    List<Subscription> findSubscriptionsByUserId(Long id);

    @Query("SELECT s FROM Subscription s WHERE s.user.id = :userId AND s.order.id = :orderId")
    List<Subscription> findSubscriptionsByUserIdAndOrderId(Long userId, Long orderId);

    void deleteSubscriptionByIdAndOrderId(Long orderId, Long subscriptionId);

    List<Subscription> findAllByOrderId(Long orderId);
}
