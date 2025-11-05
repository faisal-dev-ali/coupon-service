package com.r360.coupon_service.model.dto;

public record ApplyCouponResponse(
        boolean applied, double discount, double payable, double cashback,
        String appliedVersion // coupon version/hash for audits
) {}