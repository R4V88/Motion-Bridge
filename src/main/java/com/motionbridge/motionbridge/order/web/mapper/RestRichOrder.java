package com.motionbridge.motionbridge.order.web.mapper;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RestRichOrder {
    List<RestOrder> restOrders;

}
