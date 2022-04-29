package com.motionbridge.motionbridge.commons;

import java.math.BigDecimal;

public class PriceCalculator {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    public static BigDecimal afterDiscountApplied(BigDecimal base, BigDecimal pct) {
        return base.subtract(percentage(base, pct));
    }

    public static BigDecimal percentage(BigDecimal base, BigDecimal pct) {
        return base.multiply(pct).divide(ONE_HUNDRED);
    }

    public static BigDecimal sum(BigDecimal base, BigDecimal add) {
        return base.add(add);
    }
}
