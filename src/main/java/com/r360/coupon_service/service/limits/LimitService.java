package com.r360.coupon_service.service.limits;

import com.r360.coupon_service.exception.CouponErrors;
import com.r360.coupon_service.exception.CouponException;
import com.r360.coupon_service.model.entity.Coupon;
import com.r360.coupon_service.service.runtime.RateLimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class LimitService {

    private final RateLimitService rl;

    /**
     * 🧠 Used during validation (non-mutating).
     * Just checks that limits exist and logs projected key usage.
     */
    public void previewLimits(Coupon coupon, String userId) {
        var today = LocalDate.now();
        var cfg = coupon.getLimit();
        if (cfg == null) return;

        log.debug("[Preview] Checking limits for coupon={} user={}", coupon.getCode(), userId);

        if (cfg.getPerDayPerUser() != null)
            log.debug("Limit check → perDayPerUser={} key={}", cfg.getPerDayPerUser(),
                    LimitKeys.perUserDay(coupon.getCode(), userId, today));

        if (cfg.getPerUser() != null)
            log.debug("Limit check → perUser={} key={}", cfg.getPerUser(),
                    LimitKeys.perUser(coupon.getCode(), userId));
    }

    /**
     * 🧱 Used during apply — actually increments Redis counters and enforces limits.
     */
    public void assertWithinLimits(Coupon coupon, String userId, String deviceId) {
        var today = LocalDate.now();
        var cfg = coupon.getLimit();
        if (cfg == null) return;

        // 🔸 per-day-per-user
        if (cfg.getPerDayPerUser() != null) {
            boolean ok = rl.withinLimit(
                    LimitKeys.perUserDay(coupon.getCode(), userId, today),
                    cfg.getPerDayPerUser(),
                    Duration.ofDays(1)
            );
            if (!ok)
                throw new CouponException(CouponErrors.LIMIT_EXCEEDED, "Per-day user limit exceeded");
        }

        // 🔸 per-user lifetime
        if (cfg.getPerUser() != null) {
            boolean ok = rl.withinLimit(
                    LimitKeys.perUser(coupon.getCode(), userId),
                    cfg.getPerUser(),
                    Duration.ofDays(365 * 5)
            );
            if (!ok)
                throw new CouponException(CouponErrors.LIMIT_EXCEEDED, "Per-user lifetime limit exceeded");
        }

        // 🔸 per-device per-day
        if (deviceId != null && cfg.getPerDevice() != null) {
            boolean ok = rl.withinLimit(
                    LimitKeys.perDeviceDay(coupon.getCode(), deviceId, today),
                    cfg.getPerDevice(),
                    Duration.ofDays(1)
            );
            if (!ok)
                throw new CouponException(CouponErrors.FRAUD_BLOCKED, "Device velocity exceeded");
        }

        // 🔸 total cap (global)
        if (cfg.getTotalCap() != null) {
            boolean ok = rl.withinLimit(
                    LimitKeys.totalCap(coupon.getCode()),
                    cfg.getTotalCap(),
                    Duration.ofDays(365)
            );
            if (!ok)
                throw new CouponException(CouponErrors.LIMIT_EXCEEDED, "Global coupon redemption limit reached");
        }
    }
}
