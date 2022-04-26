package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase;
import com.motionbridge.motionbridge.order.db.OrderRepository;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.motionbridge.motionbridge.order.entity.Order.Status.NEW;

@Service
@Slf4j
@AllArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {
    final OrderRepository orderRepository;

    private final UserDataManipulationUseCase user;
    private final SubscriptionUseCase subscription;
    private final ManipulateProductUseCase product;

    @Override
    @Transactional
    public void placeOrder(PlaceOrderCommand command) {
        Long productId = command.getProductId();
        Long userId = command.getUserId();
        Order.Status status = NEW;

        ProductOrder productOrder = checkIfProductExistThenGet(productId);

        Long createdOrderId = checkIfOrderExistThenCreate(productOrder, userId, status);

        if (checkIfProductIsOnCurrentOrderList(userId, createdOrderId, productOrder, status)) {

            checkIfSubscriptionExistThenCreate(productOrder, createdOrderId, userId, status);

        } else {
            log.info("Product can not be added to current order");
        }
    }

    Long getCurrentOrderId(Long userId, Order.Status status) {
        Long orderId = 0L;
        if (orderRepository.findOrderByUserIdAndOrderStatus(userId, status).isPresent()) {
            return orderRepository.findOrderByUserIdAndOrderStatus(userId, status).get().getId();
        }
        return orderId;
    }

    Boolean checkIfProductIsOnCurrentOrderList(Long userId, Long currentOrderId, ProductOrder productOrder, Order.Status status) {
        boolean value = false;
        List<Subscription> currentSubscriptionsList;
        List<Boolean> loopValue = new ArrayList<>(Collections.emptyList());

        if (orderRepository.findOrderByUserIdAndOrderStatus(userId, status).isPresent()) {
            if (!subscription.findAllSubscriptionsByUserIdAndOrderId(userId, currentOrderId).isEmpty()) {
                currentSubscriptionsList = subscription.findAllSubscriptionsByUserIdAndOrderId(userId, currentOrderId);
                for (Subscription subscription : currentSubscriptionsList) {
                    if (subscription.getType().equals(productOrder.getName().toUpperCase())
                            &&
                            subscription.getTimePeriod().equals(productOrder.getTimePeriod().toUpperCase())) {
                        loopValue.add(false);
                    } else {
                        loopValue.add(true);
                    }
                }
                value = loopValue.contains(true);

            } else {
                value = true;
            }
        }
        return value;
    }

    /**
     * Aby utworzyć dodać subskrypcje do zamówienia:
     * - czy istnieje order o statusie new
     * - czy jest dodana subskrypcja to czy jest inna od właśnie Tworzonej (SubService.)
     */
    void checkIfSubscriptionExistThenCreate(ProductOrder productOrder, Long orderId, Long userId, Order.Status status) {
        if (orderRepository.findOrderByUserIdAndOrderStatus(userId, status).isPresent()
                && user.findById(userId).isPresent()) {

            subscription.saveSubscription(
                    NewSubscriptionCommand
                            .builder()
                            .price(productOrder.getPrice())
                            .currentPrice(productOrder.getPrice())
                            .animationsLimit(productOrder.getAnimationQuantity())
                            .type(productOrder.getName())
                            .timePeriod(productOrder.getTimePeriod())
                            .user(user.findById(userId).get())
                            .order(orderRepository.getById(orderId))
                            .build()
                            .toCreateSubscriptionCommand()
            );
        }
    }

    Long checkIfOrderExistThenCreate(ProductOrder productOrder, Long userId, Order.Status status) {
        Long orderId = 0L;
        if (orderRepository.findOrderByUserIdAndOrderStatus(userId, status).isEmpty()
                && user.findById(userId).isPresent()) {

            CreateOrderCommand command = NewOrderCommand
                    .builder()
                    .totalPrice(productOrder.getPrice())
                    .currentPrice(productOrder.getPrice())
                    .user(user.findById(userId).get())
                    .build()
                    .toCreateProductCommand();

            saveOrder(command);

            orderId = getCurrentOrderId(userId, status);
        } else {
            log.warn("Order with status NEW for user: " + userId + " already exist, redirecting to existing order...");
        }
        return orderId;
    }

    ProductOrder checkIfProductExistThenGet(Long productId) {
        ProductOrder productOrder = new ProductOrder();

        if (product.getProductById(productId).isPresent()) {

            Product temp = product.getProductById(productId).get();

            productOrder = ProductOrder
                    .builder()
                    .id(temp.getId())
                    .price(temp.getPrice())
                    .currency(temp.getCurrency().toString())
                    .animationQuantity(temp.getAnimationQuantity())
                    .name(temp.getName().toString().toUpperCase())
                    .timePeriod(temp.getTimePeriod().toString().toUpperCase())
                    .build();

            log.info("Product with Id: " + productId + " is accessible");
        } else {
            log.warn("Product with id: " + productId + " does not exist!");
        }
        return productOrder;
    }

    @Override
    public void saveOrder(CreateOrderCommand command) {
        Order order = Order
                .builder()
                .currentPrice(command.getCurrentPrice())
                .totalPrice(command.getTotalPrice())
                .user(command.getUser())
                .build();

        orderRepository.save(order);
    }

    @Data
    @Builder
    public static class NewOrderCommand {
        BigDecimal totalPrice;
        BigDecimal currentPrice;
        UserEntity user;

        CreateOrderCommand toCreateProductCommand() {
            return new CreateOrderCommand(totalPrice, currentPrice, user);
        }
    }

    @Data
    @Builder
    public static class NewSubscriptionCommand {
        BigDecimal price;
        BigDecimal currentPrice;
        Integer animationsLimit;
        String type;
        String timePeriod;
        UserEntity user;
        Order order;

        SubscriptionUseCase.CreateSubscriptionCommand toCreateSubscriptionCommand() {
            return new SubscriptionUseCase.CreateSubscriptionCommand(price, currentPrice, animationsLimit, type, timePeriod, user, order);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOrder {
        Long id;
        Integer animationQuantity;
        String name;
        String currency;
        String timePeriod;
        BigDecimal price;
    }
}
