package com.motionbridge.motionbridge.order.web;

import com.motionbridge.motionbridge.order.application.port.ApplyDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ApplyDiscountUseCase.PlaceDiscountCommand;
import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase.PlaceOrderCommand;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.RemoveSubscriptionFromOrderUseCase;
import com.motionbridge.motionbridge.order.web.mapper.RestOrder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Tag(name = "/api/order", description = "Manipulate orders")
@RestController
@RequestMapping("/api/order")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderController {

    final ManipulateOrderUseCase manipulateOrderService;
    final CreateOrderUseCase createOrderService;
    final ApplyDiscountUseCase applyDiscountService;
    final RemoveDiscountUseCase removeDiscountUseCase;
    final RemoveSubscriptionFromOrderUseCase removeSubscriptionFromOrderUseCase;

    @Operation(summary = "USER zalogowany, tworzy nowe zamówienie po id usera i id produktu")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/create")
    public void createOrder(@RequestBody RestOrderCommand restOrderCommand) {
        createOrderService.placeOrder(restOrderCommand.toPlaceOrderCommand());
    }

    @Operation(summary = "USER zalogowany, dodaje discount")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/discount")
    public void applyDiscount(@RequestBody RestApplyDiscountCommand restApplyDiscountCommand) {
        applyDiscountService.applyDiscount(restApplyDiscountCommand.toPlaceDiscountCommand());
    }

    @Operation(summary = "USER zalogowany , wyszukuje wybrany order po jego id z subskrypcjami")
    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public RestOrder getOrderById(@NotNull @PathVariable Long orderId) {
        return manipulateOrderService.getRestOrderByOrderId(orderId);
    }

    @Operation(summary = "USER zalogowany, usuwa wybrana subskrypcje po Id pod wybranym order id")
    @DeleteMapping("/{orderId}/subscription/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscription(@NotNull @PathVariable Long orderId, @NotNull @PathVariable Long subscriptionId) {
        removeSubscriptionFromOrderUseCase.deleteSubscriptionInOrderByIdAndSubscriptionId(orderId, subscriptionId);
    }

    @Operation(summary = "USER zalogowany, usuwa discount z ordera")
    @DeleteMapping("/{orderId}/removeDiscount")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDiscount(@NotNull @PathVariable Long orderId) {
        removeDiscountUseCase.removeDiscountFromOrderByOrderId(orderId);
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestOrderCommand {
        @NotNull
        Long userId;
        @NotNull
        Long productId;

        PlaceOrderCommand toPlaceOrderCommand() {
            return new PlaceOrderCommand(userId, productId);
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestApplyDiscountCommand {
        @NotBlank
        String code;
        @NotNull
        Long userId;

        PlaceDiscountCommand toPlaceDiscountCommand() {
            return new PlaceDiscountCommand(code, userId);
        }
    }
}
