package com.motionbridge.motionbridge.subscription.application;

import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.db.SubscriptionRepository;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SubscriptionService implements SubscriptionUseCase {
    private final SubscriptionRepository repository;

    @Override
    public List<Subscription> findAllSubscriptionsByUserId(Long id) {
        return repository.findSubscriptionsByUserId(id);
    }

    @Override
    public Subscription addSubscription(CreateSubscriptionCommand command) {
        Subscription subscription = toAddSubscription(command);
        repository.save(subscription);
        return subscription;
    }

    Subscription toAddSubscription(CreateSubscriptionCommand command) {
        return Subscription.builder()
                .price(command.getPrice())
                .currentPrice(command.getCurrentPrice())
                .animationsLimit(command.getAnimationLimit())
                .type(command.getType())
                .userEntity(command.getUser())
                .build();
    }
}
