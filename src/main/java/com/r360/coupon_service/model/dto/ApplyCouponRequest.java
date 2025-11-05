package com.r360.coupon_service.model.dto;

public record ApplyCouponRequest(
        String code, String userId, String orderId, String domain, double amount,
        ValidationRequest.Payment payment, String deviceId, String ip, String idempotencyKey
) {}