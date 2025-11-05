package com.r360.coupon_service.service.discount;

public interface DiscountEngine {
    boolean supports(String type);
    double compute(DiscountContext ctx);
}