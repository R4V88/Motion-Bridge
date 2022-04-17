package com.motionbridge.motionbridge.subscription.db;

import com.motionbridge.motionbridge.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findAllByUserId(Long id);
}
