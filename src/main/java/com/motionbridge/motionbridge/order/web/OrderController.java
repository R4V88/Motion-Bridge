package com.motionbridge.motionbridge.order.web;

import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase.PlaceOrderCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@Tag(name = "/api/order", description = "Manipulate orders")
@RestController
@RequestMapping("/api/order")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderController {
    final ManipulateOrderUseCase manipulateOrderUseCase;

    @Operation(summary = "ALL")
    @PostMapping("/create")
    void createOrder(@RequestBody RestOrder restOrder){
        manipulateOrderUseCase.placeOrder(restOrder.toPlaceOrderCommand());
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
