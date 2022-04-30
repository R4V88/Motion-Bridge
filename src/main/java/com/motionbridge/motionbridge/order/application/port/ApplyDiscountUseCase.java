package com.motionbridge.motionbridge.order.application.port;

import lombok.Value;

public interface ApplyDiscountUseCase {
    void applyDiscount(PlaceDiscountCommand placeDiscountCommand);

    @Value
    class PlaceDiscountCommand {
        String code;
        Long userId;
    }
}
