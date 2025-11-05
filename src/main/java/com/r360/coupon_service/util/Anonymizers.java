package com.r360.coupon_service.util;


import org.apache.commons.codec.digest.DigestUtils;

public final class Anonymizers {

    private Anonymizers() {}

    public static String sha256Ip(String ip) {
        if (ip == null || ip.isBlank()) return null;
        return DigestUtils.sha256Hex(ip.trim().toLowerCase());
    }

    public static String sha256Device(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) return null;
        return DigestUtils.sha256Hex(deviceId.trim().toLowerCase());
    }
}
