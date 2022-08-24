package com.motionbridge.motionbridge.payment;

import com.motionbridge.motionbridge.order.web.mapper.RestOrderId;
import com.motionbridge.motionbridge.security.jwt.CurrentlyLoggedUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Tag(name = "/api/orders", description = "Payments")
@RestController
@RequestMapping("/api/payments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentController {

    CurrentlyLoggedUserProvider currentlyLoggedUserProvider;


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, opłacenie zamówienia")
    @ApiResponses(value = {
            @ApiResponse(description = "Created new Order", responseCode = "201"),
            @ApiResponse(description = "Invalid arguments", responseCode = "400")
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/stripe/pay")
    public void completeOrder(@RequestBody Long orderId) {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();


    }
}
