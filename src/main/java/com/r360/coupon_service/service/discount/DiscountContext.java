package com.r360.coupon_service.service.discount;

public record DiscountContext(
        double amount,        // cart value
        double value,         // coupon value (e.g. 10 for 10% or 500 for flat)
        Double maxDiscount,   // optional max discount cap
        String domain,        // e.g., FLIGHT, HOTEL, BUS
        String type           // PERCENT, FLAT, CASHBACK
) {}
