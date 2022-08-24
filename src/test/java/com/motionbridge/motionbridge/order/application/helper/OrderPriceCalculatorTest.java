package com.motionbridge.motionbridge.order.application.helper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class OrderPriceCalculatorTest {

    @Test
    void shouldCorrectlyRecalculatePriceAfterApplyDiscount(){
        //GIVEN
        BigDecimal currentPrice = new BigDecimal("120.0");
        BigDecimal discount = new BigDecimal("30");

        //WHEN
        final BigDecimal recalculatedPrice = OrderPriceCalculator.recalculateOrderPriceAfterDiscountAppliedToOrder(currentPrice, discount);

        //THEN
        assertEquals(new BigDecimal("84.0"), recalculatedPrice);
    }

    @Test
    void shouldRecalculatePrice() {
        //GIVEN
        BigDecimal currentPrice = new BigDecimal("120.0");
        BigDecimal discount = new BigDecimal("30");

        //WHEN
        final BigDecimal recalculatedPrice = OrderPriceCalculator.recalculateOrderPriceAfterDiscountAppliedToOrder(currentPrice, discount);

        //THEN
        assertNotEquals(new BigDecimal("90.0"), recalculatedPrice);
    }

}