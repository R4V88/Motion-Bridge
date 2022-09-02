package com.motionbridge.motionbridge.subscription.web;

import com.motionbridge.motionbridge.security.jwt.CurrentlyLoggedUserProvider;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/api/subscriptions", description = "Manipulate Subscriptions")
@RestController
@RequestMapping("/api/subscriptions")
@AllArgsConstructor
public class SubscriptionController {
    final ManipulateSubscriptionUseCase manipulateSubscriptionUseCase;
    final CurrentlyLoggedUserProvider currentlyLoggedUserProvider;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Logged USER/ADMIN, changes subscription renewal status true/false")
    @ApiResponse(description = "OK", responseCode = "200")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("{subscriptionId}")
    public void changeAutoRenewStatus(@PathVariable Long subscriptionId) {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        manipulateSubscriptionUseCase.autoRenew(subscriptionId, currentLoggedUsername);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "Logged USER/ADMIN, generates animation")
    @ApiResponse(description = "OK", responseCode = "200")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("{subscriptionId}/generate")
    public void generate(@PathVariable Long subscriptionId) {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        manipulateSubscriptionUseCase.decrementAnimationsQuantity(subscriptionId, currentLoggedUsername);
    }
}
