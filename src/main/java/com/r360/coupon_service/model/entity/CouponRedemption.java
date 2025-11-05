package com.r360.coupon_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_redemptions")
@Data
public class CouponRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponCode;
    private String userId;
    private String orderId;
    private String domain;
    private Double discount;
    private String instrument;
    private String deviceId;
    private String ipHash;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime redeemedAt = LocalDateTime.now();
}
