package com.motionbridge.motionbridge.subscription.web;

import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{id}")
public class SubscriptionController {

    private final SubscriptionUseCase userSubscriptions;

    @GetMapping("/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    public List<RestSubscription> getSubscriptions(@PathVariable Long id) {
        List<RestSubscription> subscriptions = userSubscriptions
                .findAllSubscriptionsByUserId(id)
                .stream()
                .map(subscription ->
                        new RestSubscription(
                                subscription.getIsActive(),
                                subscription.getEndDate(),
                                subscription.getType(),
                                subscription.getAnimationsLimit()
                        ))
                .collect(Collectors.toList());
        if (subscriptions.size() != 0) {
            return subscriptions;
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
