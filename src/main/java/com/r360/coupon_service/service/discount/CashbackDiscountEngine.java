package com.r360.coupon_service.service.discount;

import org.springframework.stereotype.Component;

@Component
public class CashbackDiscountEngine implements DiscountEngine {
    @Override public boolean supports(String type) { return "CASHBACK".equalsIgnoreCase(type); }
    @Override public double compute(DiscountContext ctx) { return 0.0; } // payable unchanged
}
