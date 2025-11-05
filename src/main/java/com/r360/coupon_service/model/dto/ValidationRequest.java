package com.r360.coupon_service.model.dto;

import java.util.List;

public record ValidationRequest(
        String code,
        String userId,
        String domain,
        double amount,
        String channel,
        Payment payment,
        String deviceId,
        String ip,
        List<CartLine> cart
) {
    public record Payment(String mode, String bank, String bin) {}
    public record CartLine(String sku, int qty, double price, String category, String merchant) {}
}