package com.motionbridge.motionbridge.payment.web.mapper;

import lombok.Value;

import java.util.List;

@Value
public class RestPaidOrderResponse {
    long orderId;
    List<Long> subscriptionsIds;
}
