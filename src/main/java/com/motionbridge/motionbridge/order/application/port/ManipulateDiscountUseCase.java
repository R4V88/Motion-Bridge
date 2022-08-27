package com.motionbridge.motionbridge.order.application.port;

import com.motionbridge.motionbridge.commons.Either;
import com.motionbridge.motionbridge.order.entity.Discount;
import com.motionbridge.motionbridge.order.web.mapper.RestDiscount;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;

public interface ManipulateDiscountUseCase {

    List<RestDiscount> getAllDiscounts();

    CreateDiscountResponse addNewDiscount(CreateDiscountCommand discount);

    SwitchStatusResponse switchStatus(Long id);

    void deleteDiscountById(Long id);

    Discount getDiscountById(Long id);

    List<Discount> getDiscountByCode(String code);

    @Value
    class SwitchStatusResponse {
        public static SwitchStatusResponse SUCCESS = new SwitchStatusResponse(true, emptyList());

        boolean success;
        List<String> errors;
    }

    class CreateDiscountResponse extends Either<String, Discount> {

        public CreateDiscountResponse(boolean success, String left, Discount right) {
            super(success, left, right);
        }

        public static CreateDiscountResponse success(Discount right) {
            return new CreateDiscountResponse(true, null, right);
        }

        public static CreateDiscountResponse failure(String left) {
            return new CreateDiscountResponse(false, left, null);
        }
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
}
