package com.motionbridge.motionbridge.order.web;

import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase.PlaceOrderCommand;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
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

@AllArgsConstructor
@Tag(name = "/api/order", description = "Manipulate orders")
@RestController
@RequestMapping("/api/order")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderController {

    final CreateOrderUseCase createOrderUseCase;
    final SubscriptionUseCase subscription;
    final ManipulateOrderUseCase manipulateOrderUseCase;

    @Operation(summary = "USER zalogowany, tworzy nowe zamówienie po id usera i id produktu")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/create")
    void createOrder(@RequestBody RestOrder restOrder) {
        createOrderUseCase.placeOrder(restOrder.toPlaceOrderCommand());
    }

    @Operation(summary = "USER zalogowany , wyszukuje wybrany order po jego id")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    void getOrderById(@PathVariable Long id) {

    }

    @Operation(summary = "USER zalogowany, wyszukuje wszystkie subskrypcje pod wybranym order id")
    @GetMapping("/{id}/subscription")
    void getAllOrderSubscriptionsInOrderByOrderId(@PathVariable Long id) {

    }

    @Operation(summary = "USER zalogowany, wyszukuje wszystkie subskrypcje pod wybranym order id")
    @DeleteMapping("/{id}/subscription/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSubscription(@PathVariable Long id, @PathVariable Long subscriptionId) {
        if (subscription.findAllByOrderId(id).size() == 1) {
            subscription.deleteByIdAndOrderId(id, subscriptionId);
            manipulateOrderUseCase.deleteOrder(id);
        } else {
            subscription.deleteByIdAndOrderId(id, subscriptionId);
        }
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static class RestOrder {
        Long userId;
        Long productId;

        PlaceOrderCommand toPlaceOrderCommand() {
            return new PlaceOrderCommand(userId, productId);
        }
    }
}
