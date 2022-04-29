package com.motionbridge.motionbridge.order.application.port;

import com.motionbridge.motionbridge.order.entity.Discount;
import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.order.web.mapper.RestDiscount;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public interface ManipulateDiscountUseCase {
    void applyDiscount(PlaceDiscountCommand placeDiscountCommand);

    List<RestDiscount> getAllDiscounts();

    void addNewDiscount(CreateDiscountCommand discount);

    SwitchStatusResponse switchStatus(Long id);

    void deleteDiscountById(Long id);

    List<Discount> getDiscountByCode(String code);

    @Value
    class SwitchStatusResponse {
        public static ManipulateDiscountUseCase.SwitchStatusResponse SUCCESS = new ManipulateDiscountUseCase.SwitchStatusResponse(true, emptyList());

        boolean success;
        List<String> errors;
    }

    @Value
    class CreateDiscountCommand {
        String code;
        String subscriptionType;
        String subscriptionPeriod;
        LocalDateTime startDate;
        Integer duration;
        String durationPeriod;
        Integer value;
    }


    @Value
    class PlaceDiscountCommand {
        String code;
        Long userId;
        Long productId;
    }
}
