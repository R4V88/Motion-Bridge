package com.motionbridge.motionbridge.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    NEW,
    CONFIRMED,
    IN_PROGRESS,
    PAID
}
