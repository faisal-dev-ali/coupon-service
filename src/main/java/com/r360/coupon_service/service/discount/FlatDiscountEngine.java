package com.r360.coupon_service.service.discount;

import org.springframework.stereotype.Component;

@Component
public class FlatDiscountEngine implements DiscountEngine {
    @Override public boolean supports(String type) { return "FLAT".equalsIgnoreCase(type); }
    @Override public double compute(DiscountContext ctx) {
        double d = ctx.value();
        return Math.max(0, Math.min(d, ctx.maxDiscount() == null ? d : ctx.maxDiscount()));
    }
}