package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.db.OrderRepository;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.entity.Product;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase.CreateSubscriptionCommand;
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
import java.util.concurrent.atomic.AtomicInteger;

import static com.motionbridge.motionbridge.order.entity.OrderStatus.NEW;

@Service
@Slf4j
@AllArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {
    final OrderRepository orderRepository;

    private final UserDataManipulationUseCase user;
    private final SubscriptionUseCase subscription;
    private final ManipulateProductUseCase product;
    private final ManipulateOrderUseCase order;

    @Override
    @Transactional
    public void placeOrder(PlaceOrderCommand command) {
        Long productId = command.getProductId();
        Long userId = command.getUserId();
        OrderStatus status = NEW;

        //TODO Najpierw sprwdzac czy istnieje ORDER, jak nie to utworzyc i dopiero reszta logiki zamowienia
        ProductOrder productOrder = checkIfProductExistThenGet(productId);

        checkIfOrderExistThenCreate(productOrder, userId, status);

        CurrentOrder currentOrder = getCurrentOrderIfExist(userId, status);
        Long createdOrderId = currentOrder.getId();

        checkIfEqualSubscriptionAlreadyExistElseCreate(userId, createdOrderId, productOrder, status);
    }

    void checkIfEqualSubscriptionAlreadyExistElseCreate(Long userId, Long currentOrderId, ProductOrder productOrder, OrderStatus status) {
        if (order.findByUserIdAndStatus(userId, status).isPresent()) {
            Order currentOrder = order.findByUserIdAndStatus(userId, status).get();

            if (!subscription.findAllByUserIdAndOrderId(userId, currentOrderId).isEmpty()) {

                List<SubscriptionOrder> tempSubscriptionsList = new ArrayList<>(Collections.emptyList());

                for (Subscription sub : subscription.findAllByUserIdAndOrderId(userId, currentOrderId)) {
                    tempSubscriptionsList.add(toCreateSubscriptionOrder(sub));
                }
                AtomicInteger counter = new AtomicInteger();
                for (SubscriptionOrder subscriptionOrder : tempSubscriptionsList) {
                    if (subscriptionOrder.getType().equals(productOrder.getName().toUpperCase())
                            &&
                            subscriptionOrder.getTimePeriod().equals(productOrder.getTimePeriod().toUpperCase())) {
                        counter.getAndIncrement();
                    }
                }
                if(counter.get() == 0) {
                    toCreateSubscription(productOrder, userId, currentOrder);

                }else {
                    log.info("Subscription already exists");
                }
            } else {
                toCreateSubscription(productOrder, userId, currentOrder);
            }
        } else {
            log.info("No active order");
        }
    }

    void toCreateSubscription(ProductOrder productOrder, Long userId, Order order) {
        UserEntity currentUser = user.findById(userId).get();

        CreateSubscriptionCommand command = NewSubscriptionCommand
                .builder()
                .price(productOrder.getPrice())
                .currentPrice(productOrder.getPrice())
                .animationsLimit(productOrder.getAnimationQuantity())
                .type(productOrder.getName())
                .timePeriod(productOrder.getTimePeriod())
                .user(currentUser)
                .order(order)
                .build()
                .toCreateSubscriptionCommand();
        subscription.save(command);
    }

    CurrentOrder getCurrentOrderIfExist(Long userId, OrderStatus status) {
        CurrentOrder currentOrder = new CurrentOrder();
        if (orderRepository.findOrderByUserIdAndOrderStatus(userId, status).isPresent()) {
            return CurrentOrder.builder()
                    .orderStatus(orderRepository.findOrderByUserIdAndOrderStatus(userId, status).get().getStatus())
                    .id(orderRepository.findOrderByUserIdAndOrderStatus(userId, status).get().getId())
                    .build();
        } else {
            log.warn("Order id does not exist");
        }
        return currentOrder;
    }

    void checkIfOrderExistThenCreate(ProductOrder productOrder, Long userId, OrderStatus status) {
        CurrentOrder currentOrder = getCurrentOrderIfExist(userId, status);
        if (user.findById(userId).isPresent() && !currentOrder.isValid() || !currentOrder.getOrderStatus().equals(NEW)) {
            CreateOrderCommand command = NewOrderCommand
                    .builder()
                    .currentPrice(productOrder.getPrice())
                    .totalPrice(productOrder.getPrice())
                    .user(user.findById(userId).get())
                    .build()
                    .toCreateProductCommand();

            saveOrder(command);
        } else {
            log.warn("Order with status NEW for user: " + userId + " already exist");
        }
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
                    .name(String.valueOf(temp.getName()).toUpperCase())
                    .timePeriod(String.valueOf(temp.getTimePeriod()).toUpperCase())
                    .build();
            log.info("Product with Id: " + productId + " is accessible");
        } else {
            log.warn("Product with id: " + productId + " does not exist!");
        }
        return productOrder;
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

        CreateSubscriptionCommand toCreateSubscriptionCommand() {
            return new CreateSubscriptionCommand(price, currentPrice, animationsLimit, type, timePeriod, user, order);
        }
    }

    private SubscriptionOrder toCreateSubscriptionOrder(Subscription subscription) {
        return new SubscriptionOrder(subscription.getType(), subscription.getTimePeriod());
    }

    @Data
    @AllArgsConstructor
    public static class SubscriptionOrder {
        String type;
        String timePeriod;
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentOrder {
        Long id;
        OrderStatus orderStatus;

        public boolean isValid() {
            return id != null && orderStatus != null;
        }
    }
}
