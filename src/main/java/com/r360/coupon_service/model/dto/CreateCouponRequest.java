package com.r360.coupon_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateCouponRequest {
    @NotBlank private String code;
    @NotBlank private String title;
    private String description;
    @NotBlank private String domain;
    @NotBlank private String type; // FLAT, PERCENT, CASHBACK
    @NotNull private Double value;
    private Double maxDiscount;
    private Double minCartValue;
    private Boolean stackable;

    // ✅ Support timestamps with 'Z' (UTC)
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
