package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.CreateOrderUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
import com.motionbridge.motionbridge.order.db.OrderRepository;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.order.web.mapper.RestOrderId;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase.ProductOrder;
import com.motionbridge.motionbridge.security.user.UserSecurity;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase.CreateSubscriptionCommand;
import com.motionbridge.motionbridge.subscription.entity.Currency;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.motionbridge.motionbridge.order.application.helper.OrderPriceCalculator.recalculateOrderPriceAfterAddSubscription;

@Service
@Slf4j
@AllArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {
    final OrderRepository orderRepository;

    final ManipulateUserDataUseCase userService;
    final ManipulateSubscriptionUseCase subscriptionService;
    final ManipulateProductUseCase productService;
    final ManipulateDiscountUseCase discountService;
    final UserSecurity userSecurity;

    @Override
    @Transactional
    public RestOrderId placeOrder(PlaceOrderCommand command, String currentlyLoggedUserEmail) {
        final OrderStatus orderStatus = OrderStatus.NEW;
        final Long productId = command.getProductId();
        final Long userId;
        if (userService.findByUserEmailIgnoreCase(currentlyLoggedUserEmail).isPresent()) {
            userId = userService.findByUserEmailIgnoreCase(currentlyLoggedUserEmail).get().getId();
        } else {
            throw new RuntimeException("User with login: " + currentlyLoggedUserEmail + " does not exist");
        }

        UserEntity userById = userService.getCurrentUserById(userId);
        ProductOrder productOrder = productService.checkIfProductExistInOrderThenGet(productId);
        Optional<Order> order = Optional.ofNullable(getOrderElseCreate(userById, orderStatus));
        checkIfEqualSubscriptionAlreadyExistElseCreate(userById, order.get(), productOrder);

        return new RestOrderId(order.get().getId());
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
        if (counter.get() == 0 && !order.getIsLocked()) {
            toCreateSubscription(productOrder, order, user);
        } else {
            log.info("Product(Subscription) " + productOrder.getId() + " already added to order: " + order.getId());
        }
    }

    void toCreateSubscription(ProductOrder productOrder, Order order, UserEntity user) {
        CreateSubscriptionCommand command;
        if (!productOrder.getIsActive()) {
            log.info("Product with id: " + productOrder.getId() + " can not be added to order because is not available yet");
        } else {
            command = NewSubscriptionCommand
                    .builder()
                    .price(productOrder.getPrice())
                    .currentPrice(productOrder.getPrice())
                    .animationsLimit(productOrder.getAnimationQuantity())
                    .type(productOrder.getName())
                    .timePeriod(productOrder.getTimePeriod())
                    .user(user)
                    .order(order)
                    .productId(productOrder.getId())
                    .currency(Currency.valueOf(productOrder.getCurrency()))
                    .title(productOrder.getName())
                    .build()
                    .toCreateSubscriptionCommand();

            subscriptionService.save(command);
            orderRepository.save(
                    recalculateOrderPriceAfterAddSubscription(order, command)
            );
        }
    }

    Order getOrderElseCreate(UserEntity user, OrderStatus status) {
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
                CreateOrderCommand command = toCreateOrderCommand(user);
                currentOrder = saveOrder(command);
            } else {
                currentOrder = tempOrder.get();
            }
        } else {
            CreateOrderCommand command = toCreateOrderCommand(user);
            currentOrder = saveOrder(command);
        }
        return currentOrder;
    }

    CreateOrderCommand toCreateOrderCommand(UserEntity user) {
        return NewOrderCommand
                .builder()
                .user(user)
                .build()
                .toCreateProductCommand();
    }

    @Transactional
    @Override
    public Order saveOrder(CreateOrderCommand command) {
        Order order = new Order(command.getUser());
        return orderRepository.save(order);
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
        Currency currency;
        String title;
        Order order;
        Long productId;

        CreateSubscriptionCommand toCreateSubscriptionCommand() {
            return new CreateSubscriptionCommand(price, currentPrice, animationsLimit, type, timePeriod, user, currency, title, order, productId);
        }
    }

    @Data
    @AllArgsConstructor
    public static class SubscriptionOrder {
        String type;
        String timePeriod;
    }
}