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
    public List<Subscription> findAllSubscriptionsByUserId(Long id) {
        return repository.findSubscriptionsByUserId(id);
    }

    @Override
    public void saveSubscription(CreateSubscriptionCommand command) {
        Subscription subscription = toAddSubscription(command);
        repository.save(subscription);
    }

    Subscription toAddSubscription(CreateSubscriptionCommand command) {
        return Subscription.builder()
                .price(command.getPrice())
                .currentPrice(command.getCurrentPrice())
                .animationsLimit(command.getAnimationsLimit())
                .type(command.getType())
                .userEntity(command.getUser())
                .build();
    }

    @Override
    public List<Subscription> findAllSubscriptionsByUserIdAndOrderId(Long userId, Long orderId) {
        return repository.findSubscriptionsByUserIdAndOrderId(userId, orderId);
    }
}
