package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@AllArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {
    final OrderRepository orderRepository;

    private final UserDataManipulationUseCase userService;
    private final SubscriptionUseCase subscriptionService;
    private final ManipulateProductUseCase productService;
    private final ManipulateDiscountUseCase discountService;

    @Override
    @Transactional
    public void placeOrder(PlaceOrderCommand command) {
        final OrderStatus orderStatus = OrderStatus.NEW;
        final Long productId = command.getProductId();
        final Long userId = command.getUserId();

        UserEntity user = userService.getCurrentUserById(userId);
        ProductOrder productOrder = checkIfProductExistThenGet(productId);
        Order order = getOrderElseCreate(user, orderStatus, productOrder);
        checkIfEqualSubscriptionAlreadyExistElseCreate(user, order, productOrder);
    }

    void checkIfEqualSubscriptionAlreadyExistElseCreate(UserEntity user, Order order, ProductOrder productOrder) {
        List<SubscriptionOrder> tempSubscriptionsList = new ArrayList<>(Collections.emptyList());

        for (Subscription sub : subscriptionService.findAllByUserIdAndOrderId(user.getId(), order.getId())) {
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
        if (counter.get() == 0) {
            toCreateSubscription(productOrder, order, user);

        } else {
            log.info("Product(Subscription) " + productOrder.getId() + " already added to order: " + order.getId());
        }
    }

    void toCreateSubscription(ProductOrder productOrder, Order order, UserEntity user) {
        CreateSubscriptionCommand command = NewSubscriptionCommand
                .builder()
                .price(productOrder.getPrice())
                .currentPrice(productOrder.getPrice())
                .animationsLimit(productOrder.getAnimationQuantity())
                .type(productOrder.getName())
                .timePeriod(productOrder.getTimePeriod())
                .user(user)
                .order(order)
                .build()
                .toCreateSubscriptionCommand();
        subscriptionService.save(command);
    }

    Order getOrderElseCreate(UserEntity user, OrderStatus status, ProductOrder productOrder) {
        List<Order> actualOrders = orderRepository.findAllByUserId(user.getId());
        Order currentOrder;
        Optional<Order> tempOrder;

        if (!actualOrders.isEmpty()) {
            tempOrder = actualOrders
                    .stream()
                    .filter(o -> o.getStatus().equals(status))
                    .filter(o -> o.getUser().getId().equals(user.getId()))
                    .findFirst();

            if (tempOrder.isEmpty()) {
                CreateOrderCommand command = toCreateOrderCommand(productOrder, user);
                currentOrder = saveOrder(command);
            } else {
                currentOrder = tempOrder.get();
            }
        } else {
            CreateOrderCommand command = toCreateOrderCommand(productOrder, user);
            currentOrder = saveOrder(command);
        }
        return currentOrder;
    }

    CreateOrderCommand toCreateOrderCommand(ProductOrder productOrder, UserEntity user) {
        return NewOrderCommand
                .builder()
                .currentPrice(productOrder.getPrice())
                .totalPrice(productOrder.getPrice())
                .user(user)
                .build()
                .toCreateProductCommand();
    }

    @Override
    public Order saveOrder(CreateOrderCommand command) {
        Order order = Order
                .builder()
                .currentPrice(command.getCurrentPrice())
                .totalPrice(command.getTotalPrice())
                .user(command.getUser())
                .build();

        return orderRepository.save(order);
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    ProductOrder checkIfProductExistThenGet(Long productId) {
        ProductOrder productOrder = new ProductOrder();

        if (productService.getProductById(productId).isPresent()) {

            Product temp = productService.getProductById(productId).get();

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
            log.warn("Product with id: " + productId + " does not exist");
        }
        return productOrder;
    }

    private SubscriptionOrder toCreateSubscriptionOrder(Subscription subscription) {
        return new SubscriptionOrder(subscription.getType(), subscription.getTimePeriod());
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
}
