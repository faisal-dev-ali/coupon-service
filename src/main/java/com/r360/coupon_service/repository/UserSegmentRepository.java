package com.r360.coupon_service.repository;

import com.r360.coupon_service.model.entity.UserSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSegmentRepository extends JpaRepository<UserSegment, Long> {

    /**
     * Find segment by name.
     */
    UserSegment findByName(String name);
}
