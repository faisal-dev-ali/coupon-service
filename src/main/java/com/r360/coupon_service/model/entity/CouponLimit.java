package com.r360.coupon_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "coupon_limits")
@Data
public class CouponLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private Integer perUser;
    private Integer perDayPerUser;
    private Integer totalCap;
    private Integer perDevice;
    private Integer perCard;
}
