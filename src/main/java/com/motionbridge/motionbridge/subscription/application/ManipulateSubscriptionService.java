package com.motionbridge.motionbridge.subscription.application;

import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.db.SubscriptionRepository;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManipulateSubscriptionService implements ManipulateSubscriptionUseCase {
    final SubscriptionRepository repository;

    @Override
    public List<Subscription> findAllByUserId(Long id) {
        return repository.findSubscriptionsByUserId(id);
    }

    @Override
    public List<Subscription> findAllByOrderId(Long orderId) {
        return repository.findAllByOrderId(orderId);
    }

    @Override
    public List<Subscription> findAllByUserIdAndOrderId(Long userId, Long orderId) {
        return repository.findSubscriptionsByUserIdAndOrderId(userId, orderId);
    }

    @Transactional
    @Override
    public void deleteByIdAndOrderId(Long orderId, Long subscriptionId) {
        repository.deleteSubscriptionByIdAndOrderId(orderId, subscriptionId);
    }

    @Override
    public void deleteAllByUserId(Long id) {
        repository.deleteAllByUserId(id);
    }

    @Transactional
    @Override
    public AutoRenewResponse autoRenew(Long id) {
        return repository.findById(id)
                .map(product -> {
                    switchActualStatus(id);
                    return AutoRenewResponse.SUCCESS;
                })
                .orElseGet(() -> new AutoRenewResponse(false, Collections.singletonList("Could not change status")));
    }

    private void switchActualStatus(Long id) {
        Subscription subscription = repository.getById(id);
        if (subscription.getIsActive()) {
            subscription.setAutoRenew(subscription.getAutoRenew() != null && !subscription.getAutoRenew());
            log.info("Switched automatic renew of subscription " + subscription.getId() + " to: " + subscription.getAutoRenew().toString());
        } else
            throw new IllegalArgumentException("This subscription is not active yet, subscription id: " + subscription.getId());
    }

    @Transactional
    @Override
    public void save(CreateSubscriptionCommand command) {
        Subscription subscription = new Subscription(
                command.getPrice(),
                command.getCurrentPrice(),
                command.getAnimationsLimit(),
                command.getType(),
                command.getTimePeriod(),
                command.getProductId(),
                command.getUser(),
                command.getOrder()
        );
        repository.save(subscription);
    }

    @Override
    public void saveSubscription(Subscription subscription) {
        repository.save(subscription);
    }
}