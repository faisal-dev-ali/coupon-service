package com.r360.coupon_service.service.discount;

import org.springframework.stereotype.Component;

@Component
public class PercentDiscountEngine implements DiscountEngine {
    @Override public boolean supports(String type) { return "PERCENT".equalsIgnoreCase(type); }
    @Override public double compute(DiscountContext ctx) {
        double d = ctx.amount() * ctx.value() / 100.0;
        return Math.max(0, Math.min(d, ctx.maxDiscount() == null ? d : ctx.maxDiscount()));
    }
}