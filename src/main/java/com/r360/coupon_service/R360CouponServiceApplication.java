package com.r360.coupon_service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
@Slf4j
public class R360CouponServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(R360CouponServiceApplication.class, args);
	}

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        log.info("✅ JVM TimeZone set to: {}", TimeZone.getDefault().getID());
    }

}
