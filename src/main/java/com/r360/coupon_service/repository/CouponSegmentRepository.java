package com.r360.coupon_service.repository;
import com.r360.coupon_service.model.entity.CouponSegment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CouponSegmentRepository extends JpaRepository<CouponSegment, Long> {

    /**
     * Find all segments associated with a coupon.
     */
    @Query("SELECT cs.segment.name FROM CouponSegment cs WHERE cs.coupon.id = :couponId")
    List<String> findSegmentNamesByCouponId(Long couponId);
}
