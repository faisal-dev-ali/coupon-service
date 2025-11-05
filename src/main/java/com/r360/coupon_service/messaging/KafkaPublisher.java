package com.r360.coupon_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r360.coupon_service.events.CouponAppliedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPublisher {
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper om;

    public void publishCouponApplied(CouponAppliedEvent e) {
        try {
            kafka.send("coupon.applied.v1", e.couponCode(), om.writeValueAsString(e));
        } catch (Exception ex) {
            // Option A: throw to rollback txn (synchronous guarantee)
            // Option B (preferred at scale): Outbox table + background publisher
            throw new RuntimeException("Failed to publish event", ex);
        }
    }
}