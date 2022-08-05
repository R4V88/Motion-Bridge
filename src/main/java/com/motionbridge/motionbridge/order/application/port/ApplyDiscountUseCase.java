package com.motionbridge.motionbridge.order.application.port;

import com.motionbridge.motionbridge.security.user.UserEntityDetails;
import lombok.Value;

public interface ApplyDiscountUseCase {
    void applyDiscount(PlaceDiscountCommand placeDiscountCommand, UserEntityDetails user);

    @Value
    class PlaceDiscountCommand {
        String code;
        Long userId;
    }
}
