package com.r360.coupon_service.repository;

import com.r360.coupon_service.model.entity.CouponLimit;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponLimitRepository extends JpaRepository<CouponLimit, Long> {

    /**
     * Find coupon limit configuration by coupon id.
     */
    @Query("SELECT l FROM CouponLimit l WHERE l.coupon.id = :couponId")
    CouponLimit findByCouponId(Long couponId);
}

