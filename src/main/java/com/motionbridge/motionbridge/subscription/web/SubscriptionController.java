package com.motionbridge.motionbridge.subscription.web;

import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/api/subscriptions", description = "Manipulate Subscriptions")
@RestController
@RequestMapping("/api/subscriptions")
@AllArgsConstructor
public class SubscriptionController {
    final ManipulateSubscriptionUseCase manipulateSubscriptionUseCase;

    @Operation(summary = "USER zalogowany, zmiana statusu odnawiania subskrypcji")
    @PutMapping("{id}")
    public void changeAutoRenewStatus(@PathVariable Long id) {
        manipulateSubscriptionUseCase.autoRenew(id);
    }
}
