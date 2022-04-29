package com.motionbridge.motionbridge.subscription.application;

import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.db.SubscriptionRepository;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionService implements SubscriptionUseCase {
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

    @Override
    public void deleteByIdAndOrderId(Long orderId, Long subscriptionId) {
        repository.deleteSubscriptionByIdAndOrderId(orderId, subscriptionId);
    }

    @Override
    public void save(CreateSubscriptionCommand command) {
        Subscription subscription = Subscription.builder()
                .price(command.getPrice())
                .currentPrice(command.getCurrentPrice())
                .animationsLimit(command.getAnimationsLimit())
                .type(command.getType())
                .timePeriod(command.getTimePeriod())
                .user(command.getUser())
                .order(command.getOrder())
                .build();
        repository.save(subscription);
    }

    @Override
    public void saveSubscription(Subscription subscription) {
        repository.save(subscription);
    }
}
