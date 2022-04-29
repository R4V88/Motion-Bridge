package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
import com.motionbridge.motionbridge.order.application.port.ManipulateOrderUseCase;
import com.motionbridge.motionbridge.order.db.DiscountRepository;
import com.motionbridge.motionbridge.order.entity.Discount;
import com.motionbridge.motionbridge.order.entity.DurationPeriod;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.entity.OrderStatus;
import com.motionbridge.motionbridge.order.entity.SubscriptionPeriod;
import com.motionbridge.motionbridge.order.entity.SubscriptionType;
import com.motionbridge.motionbridge.order.web.mapper.RestDiscount;
import com.motionbridge.motionbridge.product.application.port.ManipulateProductUseCase;
import com.motionbridge.motionbridge.subscription.application.port.SubscriptionUseCase;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.motionbridge.motionbridge.commons.PriceCalculator.afterDiscountApplied;

@Slf4j
@AllArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountService implements ManipulateDiscountUseCase {
    static LocalDateTime calculatedEndDate;

    final DiscountRepository repository;
    final ManipulateOrderUseCase orderService;
    final SubscriptionUseCase subscriptionService;
    final UserDataManipulationUseCase userService;
    final ManipulateProductUseCase productService;

    @Override
    public void applyDiscount(PlaceDiscountCommand placeDiscountCommand) {
        final OrderStatus orderStatus = OrderStatus.NEW;

        Long productId = placeDiscountCommand.getProductId();
        Long userId = placeDiscountCommand.getUserId();
        String code = placeDiscountCommand.getCode();


        UserEntity user = userService.getCurrentUserById(userId);
//        CreateOrderService.ProductOrder productOrder = productRepository.checkIfProductExistThenGet(productId);

//        Order order = orderRepository.getOrderElseCreate(user, orderStatus, productOrder);
//        getValidDiscountToOrder(code, order);
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
        List<Discount> discounts = getDiscountByCode(code.toUpperCase());
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


    private static LocalDateTime toSetEndDate(CreateDiscountCommand command) {
        if (command.getDurationPeriod().toUpperCase().equals(DurationPeriod.DAY.toString())) {
            calculatedEndDate = command.getStartDate().plusDays(command.getDuration());
        } else if (command.getDurationPeriod().toUpperCase().equals(DurationPeriod.HOUR.toString())) {
            calculatedEndDate = command.getStartDate().plusHours(command.getDuration());
        } else {
            log.debug("Wrong duration period for discount " + command);
        }
        return calculatedEndDate;
    }

    private static Discount toDiscount(CreateDiscountCommand command) {
        calculatedEndDate = toSetEndDate(command);
        return Discount
                .builder()
                .code(command.getCode().toUpperCase())
                .subscriptionType(SubscriptionType.valueOf(command.getSubscriptionType().toUpperCase()))
                .subscriptionPeriod(SubscriptionPeriod.valueOf(command.getSubscriptionPeriod().toUpperCase()))
                .startDate(command.getStartDate())
                .durationPeriod(DurationPeriod.valueOf(command.getDurationPeriod().toUpperCase()))
                .duration(command.getDuration())
                .endDate(calculatedEndDate)
                .value(command.getValue())
                .build();
    }

    private static RestDiscount toResponseDiscount(Discount discount) {
        return RestDiscount
                .builder()
                .subscriptionType(discount.getSubscriptionType().toString())
                .subscriptionPeriod(discount.getSubscriptionPeriod().toString())
                .duration(discount.getDuration())
                .durationPeriod(discount.getDurationPeriod().toString())
                .value(discount.getValue())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .isActive(discount.getIsActive())
                .build();
    }

    @Override
    public List<RestDiscount> getAllDiscounts() {
        return repository
                .findAll()
                .stream()
                .map(DiscountService::toResponseDiscount)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void addNewDiscount(CreateDiscountCommand command) {
        repository.save(toDiscount(command));
    }

    @Transactional
    @Override
    public SwitchStatusResponse switchStatus(Long id) {
        return repository.findById(id)
                .map(product -> {
                    switchActualStatus(id);
                    return ManipulateDiscountUseCase.SwitchStatusResponse.SUCCESS;
                })
                .orElseGet(() -> new ManipulateDiscountUseCase.SwitchStatusResponse(false, Collections.singletonList("Could not change status")));
    }

    private void switchActualStatus(Long id) {
        repository.getById(id).setIsActive(repository.getById(id).getIsActive() != null && !repository.getById(id).getIsActive());
    }

    @Override
    public void deleteDiscountById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Discount> getDiscountByCode(String code) {
        return repository.findAllByCode(code);
    }
}
