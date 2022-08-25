package com.motionbridge.motionbridge.order.application.helper;

import com.motionbridge.motionbridge.order.entity.Order;
import com.motionbridge.motionbridge.subscription.entity.Subscription;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CalculatedOrderPriceDTO {
    Order order;
    List<Subscription> subscriptions;
}
