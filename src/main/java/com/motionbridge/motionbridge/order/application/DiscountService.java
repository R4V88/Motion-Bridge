package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
import com.motionbridge.motionbridge.order.db.DiscountRepository;
import com.motionbridge.motionbridge.order.entity.Discount;
import com.motionbridge.motionbridge.order.entity.Discount.DurationPeriod;
import com.motionbridge.motionbridge.order.entity.Discount.SubscriptionPeriod;
import com.motionbridge.motionbridge.order.entity.Discount.SubscriptionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountService implements ManipulateDiscountUseCase {
    static LocalDateTime calculatedEndDate;

    DiscountRepository repository;

    @Override
    public List<ResponseDiscount> getAllDiscounts() {
        return repository
                .findAll()
                .stream()
                .map(DiscountService::toResponseDiscount)
                .collect(Collectors.toList());
    }

    private static ResponseDiscount toResponseDiscount(Discount discount) {
        return ResponseDiscount
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

    @Transactional
    @Override
    public void addNewDiscount(CreateDiscountCommand command) {
        repository.save(toDiscount(command));
    }

    private static Discount toDiscount(CreateDiscountCommand command) {
        calculatedEndDate = toEndDate(command);
        return Discount
                .builder()
                .subscriptionType(SubscriptionType.valueOf(command.getSubscriptionType().toUpperCase()))
                .subscriptionPeriod(SubscriptionPeriod.valueOf(command.getSubscriptionPeriod().toUpperCase()))
                .startDate(command.getStartDate())
                .durationPeriod(DurationPeriod.valueOf(command.getDurationPeriod().toUpperCase()))
                .duration(command.getDuration())
                .endDate(calculatedEndDate)
                .value(command.getValue())
                .build();
    }

    private static LocalDateTime toEndDate(CreateDiscountCommand command) {
        if (command.getDurationPeriod().toUpperCase().equals(DurationPeriod.DAY.toString())) {
            calculatedEndDate = command.getStartDate().plusDays(command.getDuration());
        } else if (command.getDurationPeriod().toUpperCase().equals(DurationPeriod.HOUR.toString())) {
            calculatedEndDate = command.getStartDate().plusHours(command.getDuration());
        } else {
            log.debug("Wrong duration period for discount " + command);
        }
        return calculatedEndDate;
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
}
