package com.motionbridge.motionbridge.order.application;

import com.motionbridge.motionbridge.order.application.port.ManipulateDiscountUseCase;
import com.motionbridge.motionbridge.order.db.DiscountRepository;
import com.motionbridge.motionbridge.order.entity.Discount;
import com.motionbridge.motionbridge.order.entity.DurationPeriod;
import com.motionbridge.motionbridge.order.entity.SubscriptionPeriod;
import com.motionbridge.motionbridge.order.entity.SubscriptionType;
import com.motionbridge.motionbridge.order.web.mapper.RestDiscount;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ManipulateDiscountService implements ManipulateDiscountUseCase {
    static LocalDateTime calculatedEndDate;

    final DiscountRepository repository;

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
                .map(ManipulateDiscountService::toResponseDiscount)
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

    @Transactional
    @Override
    public void deleteDiscountById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Discount getDiscountById(Long id) {
        return repository.getById(id);
    }

    @Override
    public List<Discount> getDiscountByCode(String code) {
        return repository.findAllByCode(code);
    }
}
