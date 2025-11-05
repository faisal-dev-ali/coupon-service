package com.r360.coupon_service.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String title;
    private String description;
    private String domain;
    private String type; // PERCENT, FLAT, CASHBACK
    private Double value;
    private Double maxDiscount;
    private Double minCartValue;
    private Boolean stackable;

    // ✅ Use timezone-aware timestamps
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime startAt;
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime endAt;

    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Column(columnDefinition = "json")
    private String meta;
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "coupon", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private CouponLimit limit;
}
