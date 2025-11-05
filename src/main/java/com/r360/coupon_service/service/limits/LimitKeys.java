package com.r360.coupon_service.service.limits;

import java.time.LocalDate;

public final class LimitKeys {

    private LimitKeys() {}

    public static String perUserDay(String code, String userId, LocalDate date) {
        return "limit:user:day:" + code + ":" + userId + ":" + date;
    }

    public static String perUser(String code, String userId) {
        return "limit:user:life:" + code + ":" + userId;
    }

    public static String perDeviceDay(String code, String deviceId, LocalDate date) {
        return "limit:device:day:" + code + ":" + deviceId + ":" + date;
    }

    public static String totalCap(String code) {
        return "limit:global:" + code;
    }
}
