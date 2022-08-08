package com.motionbridge.motionbridge.order.web;

import com.motionbridge.motionbridge.order.application.port.ApplyDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ApplyDiscountUseCase.PlaceDiscountCommand;
import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase.PlaceOrderCommand;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveSubscriptionFromOrderUseCase;
import com.motionbridge.motionbridge.order.web.mapper.RestOrder;
import com.motionbridge.motionbridge.security.user.UserEntityDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "USER zalogowany, tworzy nowe zamówienie po id usera i id produktu")
    @ApiResponses(value = {
            @ApiResponse(description = "Created new Order", responseCode = "201"),
            @ApiResponse(description = "Invalid arguments", responseCode = "400")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create")
    public void createOrder(@Valid @RequestBody RestOrderCommand restOrderCommand, @AuthenticationPrincipal UserEntityDetails user) {
        createOrderService.placeOrder(restOrderCommand.toPlaceOrderCommand(), user);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "USER zalogowany, dodaje discount")
    @ApiResponse(description = "Successfully added a discount", responseCode = "200")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/discount")
    public void applyDiscount(@Valid @RequestBody RestApplyDiscountCommand restApplyDiscountCommand, @AuthenticationPrincipal UserEntityDetails user) {
        if (restApplyDiscountCommand.code.equalsIgnoreCase("TEAPOT")) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Sorry can't help you, I'm a teapot");
        }
        applyDiscountService.applyDiscount(restApplyDiscountCommand.toPlaceDiscountCommand(), user);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "USER zalogowany , wyszukuje wybrany order po jego id z subskrypcjami")
    @ApiResponse(description = "When order successfully found", responseCode = "200")
    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public RestOrder getOrderById(@NotNull @PathVariable Long orderId, @AuthenticationPrincipal UserEntityDetails user) {
        return manipulateOrderService
                .getRestOrderByOrderId(orderId, user);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "USER zalogowany, usuwa wybrana subskrypcje po Id pod wybranym order id")
    @ApiResponse(description = "When subscription Successfully deleted", responseCode = "204")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}/subscription/{subscriptionId}")
    public void deleteSubscription(@NotNull @PathVariable Long orderId, @NotNull @PathVariable Long subscriptionId, @AuthenticationPrincipal UserEntityDetails user) {
        removeSubscriptionFromOrderUseCase.deleteSubscriptionInOrderByIdAndSubscriptionId(orderId, subscriptionId, user);
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @Operation(summary = "USER zalogowany, usuwa discount z ordera")
    @ApiResponse(description = "When subscription Successfully deleted", responseCode = "204")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}/removeDiscount")
    public void removeDiscount(@NotNull @PathVariable Long orderId, @AuthenticationPrincipal UserEntityDetails user) {
        removeDiscountUseCase.removeDiscountFromOrderByOrderId(orderId, user);
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
