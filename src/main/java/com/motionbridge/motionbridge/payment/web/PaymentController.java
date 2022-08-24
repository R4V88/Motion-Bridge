package com.motionbridge.motionbridge.payment.web;

import com.motionbridge.motionbridge.payment.application.port.PaymentUseCase;
import com.motionbridge.motionbridge.payment.web.mapper.RestPaidOrderResponse;
import com.motionbridge.motionbridge.security.jwt.CurrentlyLoggedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Tag(name = "/api/payments", description = "Payments")
@RestController
@RequestMapping("/api/payments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentController {

    CurrentlyLoggedUserProvider currentlyLoggedUserProvider;
    PaymentUseCase paymentUseCase;


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, opłacenie zamówienia")
    @ApiResponses(value = {
            @ApiResponse(description = "Order paid", responseCode = "200"),
            @ApiResponse(description = "Invalid arguments", responseCode = "400")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/stripe/pay{orderId}")
    public ResponseEntity<RestPaidOrderResponse> completeOrder(@RequestBody RequestOrderId orderId) {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        final RestPaidOrderResponse restPaidOrderResponse = paymentUseCase.pay(orderId.getOrderId(), currentLoggedUsername);
        if (restPaidOrderResponse.getSubscriptionsIds().size() != 0) {
            return ResponseEntity.status(HttpStatus.OK).body(restPaidOrderResponse);
        }
        return ResponseEntity.badRequest().build();
    }

    @Data
    static class RequestOrderId {
        long orderId;
    }
}
