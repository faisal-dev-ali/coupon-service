package com.r360.coupon_service.repository;

import com.r360.coupon_service.model.entity.CouponRedemption;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RedemptionRepository extends JpaRepository<CouponRedemption, Long> {

    /**
     * Count how many times a user redeemed a coupon.
     */
    @Query("SELECT COUNT(r) FROM CouponRedemption r WHERE r.userId = :userId AND r.couponCode = :code")
    long countUserRedemptions(@Param("userId") String userId, @Param("code") String code);

    /**
     * Count redemptions for a coupon today (for per-day limits).
     */
    @Query("SELECT COUNT(r) FROM CouponRedemption r WHERE r.couponCode = :code " +
            "AND DATE(r.redeemedAt) = CURRENT_DATE")
    long countDailyRedemptions(@Param("code") String code);

    /**
     * Prevent duplicate redemption per order.
     */
    boolean existsByCouponCodeAndOrderId(String code, String orderId);

    /**
     * Recent redemptions by user (for analytics dashboards).
     */
    List<CouponRedemption> findTop10ByUserIdOrderByRedeemedAtDesc(String userId);

    /**
     * Fetch redemptions within time window for rate limiting.
     */
    @Query("SELECT r FROM CouponRedemption r WHERE r.userId = :userId AND r.redeemedAt >= :since")
    List<CouponRedemption> findRecentRedemptions(@Param("userId") String userId,
                                                 @Param("since") LocalDateTime since);
}
