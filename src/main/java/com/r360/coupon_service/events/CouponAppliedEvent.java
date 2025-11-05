package com.r360.coupon_service.events;

import com.r360.coupon_service.model.entity.CouponRedemption;

import java.time.Instant;

public record CouponAppliedEvent(
        String couponCode, String userId, String orderId, String domain,
        double discount, double cashback, Instant ts
) {
    public static CouponAppliedEvent of(CouponRedemption r, double cashback) {
        return new CouponAppliedEvent(r.getCouponCode(), r.getUserId(), r.getOrderId(),
                r.getDomain(), r.getDiscount(), cashback, Instant.now());
    }
}