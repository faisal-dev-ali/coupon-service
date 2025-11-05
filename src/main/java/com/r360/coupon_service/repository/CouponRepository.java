package com.r360.coupon_service.repository;

import com.r360.coupon_service.model.entity.Coupon;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /**
     * 🔹 Fetch active coupon by code with campaign details eagerly.
     */
    @Query("SELECT c FROM Coupon c JOIN FETCH c.campaign WHERE c.code = :code AND c.active = TRUE")
    Optional<Coupon> findActiveByCode(@Param("code") String code);

    /**
     * 🔹 Fetch coupon by code regardless of active state.
     */
    Optional<Coupon> findByCode(String code);

    List<Coupon> findByDomainAndActive(String domain, Boolean active);

    /**
     * 🔹 List all currently active coupons for a domain (for caching / discovery).
     */
    @Query("""
        SELECT c FROM Coupon c
         WHERE c.domain = :domain
           AND c.active = TRUE
           AND c.startAt <= :now
           AND c.endAt >= :now
         ORDER BY c.startAt DESC
    """)
    List<Coupon> findActiveCouponsByDomain(@Param("domain") String domain, @Param("now") LocalDateTime now);

    /**
     * 🔹 Admin search with optional filters (domain, active flag, date range).
     */
    @Query("""
        SELECT c FROM Coupon c
         WHERE (:domain IS NULL OR c.domain = :domain)
           AND (:active IS NULL OR c.active = :active)
           AND (c.startAt <= :endDate AND c.endAt >= :startDate)
    """)
    List<Coupon> searchCoupons(@Param("domain") String domain,
                               @Param("active") Boolean active,
                               @Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    /**
     * 🔹 Soft delete (mark inactive).
     */
    @Modifying
    @Transactional
    @Query("UPDATE Coupon c SET c.active = FALSE, c.updatedAt = CURRENT_TIMESTAMP WHERE c.code = :code")
    int deactivateCoupon(@Param("code") String code);

    /**
     * 🔹 Hard delete by code (rare, usually admin-only).
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Coupon c WHERE c.code = :code")
    int deleteByCode(@Param("code") String code);

    /**
     * 🔹 Find all coupons expiring soon (for cron jobs / notifications).
     */
    @Query("""
        SELECT c FROM Coupon c
         WHERE c.active = TRUE
           AND c.endAt BETWEEN :from AND :to
    """)
    List<Coupon> findCouponsExpiringSoon(@Param("from") LocalDateTime from,
                                         @Param("to") LocalDateTime to);

    /**
     * 🔹 Find all coupons that have expired (for cleanup / archival job).
     */
    @Query("""
        SELECT c FROM Coupon c
         WHERE c.active = TRUE
           AND c.endAt < :now
    """)
    List<Coupon> findExpiredCoupons(@Param("now") LocalDateTime now);

    /**
     * 🔹 Bulk deactivate expired coupons (cron-based cleanup).
     */
    @Modifying
    @Transactional
    @Query("UPDATE Coupon c SET c.active = FALSE, c.updatedAt = CURRENT_TIMESTAMP WHERE c.active = TRUE AND c.endAt < :now")
    int deactivateExpiredCoupons(@Param("now") LocalDateTime now);

    /**
     * 🔹 Find all active coupons linked to a given campaign (for campaign dashboard).
     */
    @Query("SELECT c FROM Coupon c WHERE c.campaign.id = :campaignId AND c.active = TRUE")
    List<Coupon> findByCampaignId(@Param("campaignId") Long campaignId);

    /**
     * 🔹 Domain + Type lookup (e.g., FLIGHT + PERCENT coupons).
     */
    @Query("""
        SELECT c FROM Coupon c
         WHERE c.domain = :domain
           AND c.type = :type
           AND c.active = TRUE
    """)
    List<Coupon> findActiveCouponsByDomainAndType(@Param("domain") String domain,
                                                  @Param("type") String type);

    /**
     * 🔹 Bulk deactivate coupons by campaign ID (when campaign ends).
     */
    @Modifying
    @Transactional
    @Query("UPDATE Coupon c SET c.active = FALSE, c.updatedAt = CURRENT_TIMESTAMP WHERE c.campaign.id = :campaignId")
    int deactivateByCampaignId(@Param("campaignId") Long campaignId);
}
