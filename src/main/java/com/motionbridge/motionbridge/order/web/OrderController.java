package com.motionbridge.motionbridge.order.web;

import com.motionbridge.motionbridge.order.application.port.ApplyDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ApplyDiscountUseCase.PlaceDiscountCommand;
import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase.PlaceOrderCommand;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveSubscriptionFromOrderUseCase;
import com.motionbridge.motionbridge.order.web.mapper.RestOrder;
import com.motionbridge.motionbridge.order.web.mapper.RestOrderId;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Tag(name = "/api/orders", description = "Manipulate orders")
@RestController
@RequestMapping("/api/orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderController {

    final ManipulateOrderUseCase manipulateOrderService;
    final CreateOrderUseCase createOrderService;
    final ApplyDiscountUseCase applyDiscountService;
    final RemoveDiscountUseCase removeDiscountUseCase;
    final RemoveSubscriptionFromOrderUseCase removeSubscriptionFromOrderUseCase;
    final CurrentlyLoggedUserProvider currentlyLoggedUserProvider;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, tworzy nowe zamówienie dla wybranego id produktu")
    @ApiResponses(value = {
            @ApiResponse(description = "Created new Order", responseCode = "201"),
            @ApiResponse(description = "Invalid arguments", responseCode = "400")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public ResponseEntity<RestOrderId> createOrder(@Valid @RequestBody RestOrderCommand restOrderCommand) {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        final RestOrderId orderId = createOrderService.placeOrder(restOrderCommand.toPlaceOrderCommand(), currentLoggedUsername);

        if (orderId.getOrderId() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);

    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, dodaje discount do zamowienia")
    @ApiResponse(description = "Successfully added a discount", responseCode = "200")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/discount")
    public void applyDiscount(@Valid @RequestBody RestApplyDiscountCommand restApplyDiscountCommand) {
        if (restApplyDiscountCommand.code.equalsIgnoreCase("TEAPOT")) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Sorry can't help you, I'm a teapot");
        }
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        applyDiscountService.applyDiscount(restApplyDiscountCommand.toPlaceDiscountCommand(), currentLoggedUsername);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany , wyszukuje wybrany order po jego id z subskrypcjami")
    @ApiResponse(description = "When order successfully found", responseCode = "200")
    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public RestOrder getOrderById(@NotNull @PathVariable Long orderId) {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        return manipulateOrderService
                .getRestOrderByOrderId(orderId, currentLoggedUsername);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, usuwa wybrana subskrypcje po Id pod wybranym order id")
    @ApiResponse(description = "When subscription Successfully deleted", responseCode = "204")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}/subscriptions/{subscriptionId}")
    public void deleteSubscription(@NotNull @PathVariable Long orderId, @NotNull @PathVariable Long subscriptionId) {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        removeSubscriptionFromOrderUseCase.deleteSubscriptionInOrderByIdAndSubscriptionId(orderId, subscriptionId, currentLoggedUsername);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Operation(summary = "USER zalogowany, usuwa discount z ordera")
    @ApiResponse(description = "When subscription Successfully deleted", responseCode = "204")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}/removeDiscount")
    public void removeDiscount(@NotNull @PathVariable Long orderId) {
        final String currentLoggedUsername = currentlyLoggedUserProvider.getCurrentLoggedUsername();
        removeDiscountUseCase.removeDiscountFromOrderByOrderId(orderId, currentLoggedUsername);
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestOrderCommand {
        @NotNull
        Long productId;

        PlaceOrderCommand toPlaceOrderCommand() {
            return new PlaceOrderCommand(productId);
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestApplyDiscountCommand {
        @NotBlank(message = "Please provide valid code")
        String code;

        PlaceDiscountCommand toPlaceDiscountCommand() {
            return new PlaceDiscountCommand(code);
        }
    }


}
