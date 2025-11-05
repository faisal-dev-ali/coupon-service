package com.r360.coupon_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "coupon_rules")
@Data
public class CouponRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    private String ruleType; // SPEL, DROOLS, JSON
    @Lob
    private String ruleExpr;
    private Integer priority;
}
