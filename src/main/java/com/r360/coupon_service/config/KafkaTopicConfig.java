package com.r360.coupon_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic couponAppliedTopic() {
        return new NewTopic("coupon.applied.v1", 6, (short) 1);
    }

    @Bean
    public NewTopic couponRedeemedTopic() {
        return new NewTopic("coupon.redeemed.v1", 6, (short) 1);
    }
}
