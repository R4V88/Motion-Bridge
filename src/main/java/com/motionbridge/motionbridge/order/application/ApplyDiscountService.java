package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.ApplyDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.entity.Discount;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.SubscriptionType;
import com.motionbridge.motionbridge.security.user.UserEntityDetails;
import com.motionbridge.motionbridge.security.user.UserSecurity;
import com.motionbridge.motionbridge.subscription.application.port.ManipulateSubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.users.application.port.ManipulateUserDataUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.motionbridge.motionbridge.order.application.helper.OrderPriceCalculator.recalculateOrderPriceAfterDiscountAppliedToOrder;
import static com.motionbridge.motionbridge.order.application.helper.OrderPriceCalculator.recalculateOrderPricesAfeterAddDiscountToSubscription;

@Service
@AllArgsConstructor
@Slf4j
public class ApplyDiscountService implements ApplyDiscountUseCase {

    final ManipulateOrderUseCase orderService;
    final ManipulateSubscriptionUseCase subscriptionService;
    final ManipulateDiscountUseCase discountService;
    final UserSecurity userSecurity;
    final ManipulateUserDataUseCase manipulateUserDataUseCase;

    @Override
    public void applyDiscount(PlaceDiscountCommand placeDiscountCommand, UserEntityDetails user) {
        Long userId;

        if (manipulateUserDataUseCase.findByUserEmailIgnoreCase(user.getUsername()).isPresent()) {
            userId = manipulateUserDataUseCase.findByUserEmailIgnoreCase(user.getUsername()).get().getId();
        } else {
            throw new RuntimeException("User with login: " + user.getUsername() + " does not exist");
        }

        String code = placeDiscountCommand.getCode();

        if (userSecurity.isOwnerOrAdmin(manipulateUserDataUseCase.getUserById(userId).get().getEmail(), user)) {
            Order order = orderService.getOrderWithStatusNewByUserId(userId);
            getValidDiscountToOrder(code, order);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    public void getValidDiscountToOrder(String code, Order order) {
        Discount availableDiscount = getAvailableDiscount(code, order);
        Long availableDiscountId = availableDiscount.getId();
        Optional<Subscription> foundSubscription;
        Subscription availableSubscription;

        if (!order.getActiveDiscount() && !order.getIsLocked() && availableDiscount.getCode().equals(code.toUpperCase())) {
            if (availableDiscount.getSubscriptionType().equals(SubscriptionType.ALL)) {

                BigDecimal currentPrice = recalculateOrderPriceAfterDiscountAppliedToOrder(order.getCurrentPrice(), BigDecimal.valueOf(availableDiscount.getValue()));

                order.setCurrentPrice(currentPrice);
                order.setDiscountId(availableDiscountId);
                order.setActiveDiscount(true);
                orderService.save(order);
            } else {
                List<Subscription> actualSubsricptions = subscriptionService.findAllByOrderId(order.getId());

                if (!actualSubsricptions.isEmpty()) {
                    foundSubscription = actualSubsricptions
                            .stream()
                            .filter(sub -> sub.getType().equals(availableDiscount.getSubscriptionType().toString()))
                            .filter(sub -> sub.getTimePeriod().equals(availableDiscount.getSubscriptionPeriod().toString()))
                            .findFirst();
                    if (foundSubscription.isPresent()) {
                        availableSubscription = foundSubscription.get();
                        availableSubscription
                                .setCurrentPrice(recalculateOrderPriceAfterDiscountAppliedToOrder(availableSubscription.getPrice(), BigDecimal.valueOf(availableDiscount.getValue())));

                        subscriptionService.saveSubscription(availableSubscription);

                        orderService.save(
                                recalculateOrderPricesAfeterAddDiscountToSubscription(order, subscriptionService.findAllByOrderId(order.getId()), availableDiscountId)
                        );
                    } else
                        throw new IllegalArgumentException("Subscription is not compatible to given discount: " + code);

                } else
                    throw new NullPointerException("No valid subscriptions in order " + order.getId() + " to add discount: " + code);
            }

        } else
            throw new IllegalArgumentException("Your order is not comaptible with given discount: " + code);
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
        } else
            throw new NoSuchElementException("No available discount equal to privided code: " + code);
        return discount;
    }

}