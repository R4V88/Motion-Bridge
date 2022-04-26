package com.motionbridge.motionbridge.subscription.web;

import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "/api/user", description = "Manipulate Subscriptions")
@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class SubscriptionController {

    final SubscriptionUseCase userSubscriptions;

    @Operation(summary = "USER zalogowany")
    @GetMapping("/{id}/subscription")
    @ResponseStatus(HttpStatus.OK)
    public List<RestSubscription> getSubscriptions(@PathVariable Long id) {
        List<RestSubscription> subscriptions = userSubscriptions
                .findAllByUserId(id)
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
