package com.r360.coupon_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "coupon_segments")
@Data
public class CouponSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id")
    private UserSegment segment;
}
