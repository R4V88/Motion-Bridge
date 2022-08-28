package com.motionbridge.motionbridge.order.application.port;

import lombok.Value;

public interface ApplyDiscountUseCase {
    void applyDiscount(PlaceDiscountCommand placeDiscountCommand, String userEmail);

    @Value
    class PlaceDiscountCommand {
        String code;
    }
}
