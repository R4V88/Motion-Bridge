package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.ApplyDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.entity.Discount;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.order.entity.SubscriptionType;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase.ProductOrder;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.motionbridge.motionbridge.commons.PriceCalculator.afterDiscountApplied;

@Service
@AllArgsConstructor
@Slf4j
public class ApplyDiscountService implements ApplyDiscountUseCase {

    final ManipulateOrderUseCase orderService;
    final SubscriptionUseCase subscriptionService;
    final UserDataManipulationUseCase userService;
    final ManipulateProductUseCase productService;
    final ManipulateDiscountUseCase discountService;

    @Override
    public void applyDiscount(PlaceDiscountCommand placeDiscountCommand) {
        final OrderStatus orderStatus = OrderStatus.NEW;

        Long productId = placeDiscountCommand.getProductId();
        Long userId = placeDiscountCommand.getUserId();
        String code = placeDiscountCommand.getCode();
        Order order = orderService.getOrderWithStatusNewByUserId(userId);

        UserEntity user = userService.getCurrentUserById(userId);
        ProductOrder productOrder = productService.checkIfProductExistInOrderThenGet(productId);

        getValidDiscountToOrder(code, order);
    }

    public void getValidDiscountToOrder(String code, Order order) {
        Discount availableDiscount = getAvailableDiscount(code, order);
        Optional<Subscription> foundSubscription;
        Subscription availableSubscription;

        if (!order.getActiveDiscount() && !order.getIsLocked() && !availableDiscount.getCode().equals("")) {
            if (availableDiscount.getSubscriptionType().equals(SubscriptionType.ALL)) {

                BigDecimal currentPrice = afterDiscountApplied(order.getCurrentPrice(), BigDecimal.valueOf(availableDiscount.getValue()));

                order.setCurrentPrice(currentPrice);
                order.setDiscountId(availableDiscount.getId());
                order.setActiveDiscount(true);
                orderService.save(order);
            } else {
                List<Subscription> actualSubsricptions = subscriptionService.findAllByOrderId(order.getId());

                if (!actualSubsricptions.isEmpty()) {
                    foundSubscription = actualSubsricptions
                            .stream()
                            .filter(sub -> sub.getType().equals(availableDiscount.getSubscriptionType().toString()))
                            .filter(sub -> sub.getTimePeriod().equals(availableDiscount.getDurationPeriod().toString()))
                            .findFirst();
                    if (foundSubscription.isPresent()) {
                        availableSubscription = foundSubscription.get();
                        availableSubscription
                                .setCurrentPrice(afterDiscountApplied(availableSubscription.getPrice(), BigDecimal.valueOf(availableDiscount.getValue())));
                        subscriptionService.saveSubscription(availableSubscription);
                    } else {
                        log.info("No valid subscription in order " + order.getId() + " to add discount: " + code);
                    }

                } else {
                    log.info("No subscriptions in order: " + order.getId());
                }
            }
        }
    }

    public Discount getAvailableDiscount(String code, Order order) {
        List<Discount> discounts = discountService.getDiscountByCode(code.toUpperCase());
        Discount discount;

        Optional<Discount> dsc = discounts
                .stream()
                .filter(Discount::getIsActive)
                .filter(d -> d.getStartDate().isBefore(order.getCreatedAt()))
                .filter(d -> d.getEndDate().isAfter(LocalDateTime.now()))
                .findFirst();

        if (dsc.isPresent()) {
            discount = dsc.get();
        } else {
            log.info("No available discount for provided code " + code);
            discount = new Discount();
        }
        return discount;
    }

}
