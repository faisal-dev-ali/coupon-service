package com.r360.coupon_service.repository;


import com.r360.coupon_service.model.entity.CouponRule;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CouponRuleRepository extends JpaRepository<CouponRule, Long> {

    /**
     * Fetch all rules for a given coupon ordered by priority.
     */
    @Query("SELECT r FROM CouponRule r WHERE r.coupon.id = :couponId ORDER BY r.priority ASC")
    List<CouponRule> findByCouponIdOrdered(Long couponId);

    /**
     * Count rules by type (for monitoring).
     */
    @Query("SELECT COUNT(r) FROM CouponRule r WHERE r.ruleType = :type")
    long countRulesByType(String type);
}
