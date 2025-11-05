package com.r360.coupon_service.model.dto;

import com.r360.coupon_service.model.entity.Coupon;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CouponResponse {
    private String code;
    private String title;
    private String domain;
    private String type;
    private Double value;
    private Double maxDiscount;
    private Boolean active;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public static CouponResponse from(Coupon c) {
        return CouponResponse.builder()
                .code(c.getCode())
                .title(c.getTitle())
                .domain(c.getDomain())
                .type(c.getType())
                .value(c.getValue())
                .maxDiscount(c.getMaxDiscount())
                .active(c.getActive())
                .startAt(c.getStartAt())
                .endAt(c.getEndAt())
                .build();
    }
}
