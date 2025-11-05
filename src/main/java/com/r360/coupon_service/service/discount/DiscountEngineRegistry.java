package com.r360.coupon_service.service.discount;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiscountEngineRegistry {
    private final List<DiscountEngine> engines;
    public DiscountEngineRegistry(List<DiscountEngine> engines) {
        this.engines = engines;
    }

    public DiscountEngine get(String type) {
        return engines.stream().filter(e -> e.supports(type))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown discount type: " + type));
    }
}