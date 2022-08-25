package com.motionbridge.motionbridge.subscription.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TimePeriod {
    MONTH(30),
    YEAR(360);

    private Integer period;
}