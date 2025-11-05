package com.r360.coupon_service.repository;

import com.r360.coupon_service.model.entity.Campaign;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    /**
     * Fetch all active campaigns for today.
     */
    @Query("SELECT c FROM Campaign c WHERE c.active = TRUE " +
            "AND c.startAt <= :now AND c.endAt >= :now")
    List<Campaign> findActiveCampaigns(LocalDateTime now);
}
