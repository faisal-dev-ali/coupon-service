package com.r360.coupon_service.model.dto;

import java.util.Map;

public record ValidationResult(
        boolean valid,
        String message,
        Double discount,         // null if invalid
        Double payable,          // null if invalid
        Map<String, Object> meta // remaining limits, campaign info
) {}