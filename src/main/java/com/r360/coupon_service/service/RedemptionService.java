package com.r360.coupon_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.r360.coupon_service.events.CouponAppliedEvent;
import com.r360.coupon_service.exception.CouponErrors;
import com.r360.coupon_service.exception.CouponException;
import com.r360.coupon_service.messaging.KafkaPublisher;
import com.r360.coupon_service.model.dto.ApplyCouponRequest;
import com.r360.coupon_service.model.dto.ApplyCouponResponse;
import com.r360.coupon_service.model.dto.ValidationRequest;
import com.r360.coupon_service.model.entity.Coupon;
import com.r360.coupon_service.model.entity.CouponRedemption;
import com.r360.coupon_service.repository.CouponRepository;
import com.r360.coupon_service.repository.RedemptionRepository;
import com.r360.coupon_service.service.discount.DiscountContext;
import com.r360.coupon_service.service.discount.DiscountEngineRegistry;
import com.r360.coupon_service.service.runtime.IdempotencyService;
import com.r360.coupon_service.util.Anonymizers;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

// service/RedemptionService.java
@Service
@RequiredArgsConstructor
public class RedemptionService {
    private final ValidationService validationService;
    private final RedemptionRepository redemptionRepo;
    private final CouponRepository couponRepo;
    private final IdempotencyService idempotency;
    private final DiscountEngineRegistry discountRegistry;
    private final KafkaPublisher publisher;        // see below
    private final ObjectMapper om;

    @Transactional
    public ApplyCouponResponse apply(ApplyCouponRequest req) {
        // idempotency (avoid duplicate apply on retries)
        var payloadHash = DigestUtils.sha256Hex(req.toString());
        if (!idempotency.acquire(req.idempotencyKey(), Duration.ofHours(24), payloadHash)) {
            // return same effect for replays OR verify stored outcome
            throw new CouponException(CouponErrors.IDEMPOTENT_REPLAY, "Duplicate request");
        }

        // duplicate order guard
        if (redemptionRepo.existsByCouponCodeAndOrderId(req.code(), req.orderId()))
            throw new CouponException(CouponErrors.DUPLICATE_ORDER, "Coupon already applied to this order");

        // re-validate (authoritative)
        var vres = validationService.validate(new ValidationRequest(
                req.code(), req.userId(), req.domain(), req.amount(),
                "APP", new ValidationRequest.Payment(req.payment().mode(), req.payment().bank(), req.payment().bin()),
                req.deviceId(), req.ip(), List.of()
        ));

        // load coupon for type/value
        var coupon = couponRepo.findActiveByCode(req.code())
                .orElseThrow(() -> new CouponException(CouponErrors.INVALID_CODE, "Invalid coupon"));

        // enforce runtime limits AFTER validation but BEFORE commit (atomic)
        // (use LimitService with Redis to track per-user/device/day)
        // limitService.assertWithinLimits(coupon, req.userId(), req.deviceId()); // <- plug in

        // compute discount again to lock record (safety)
        var engine = discountRegistry.get(coupon.getType());
        double discount = engine.compute(new DiscountContext(
                req.amount(),
                coupon.getValue(),
                coupon.getMaxDiscount(),
                coupon.getDomain(),
                coupon.getType()
        ));
        double payable = Math.max(0, req.amount() - discount);
        double cashback = "CASHBACK".equalsIgnoreCase(coupon.getType()) ? computeCashback(coupon, req.amount()) : 0.0;

        // persist redemption (unique (coupon_code, order_id) enforced by index)
        var r = new CouponRedemption();
        r.setCouponCode(coupon.getCode());
        r.setUserId(req.userId());
        r.setOrderId(req.orderId());
        r.setDomain(req.domain());
        r.setDiscount(discount);
        r.setInstrument(req.payment().mode());
        r.setDeviceId(req.deviceId());
        r.setIpHash(Anonymizers.sha256Ip(req.ip()));
        redemptionRepo.saveAndFlush(r);

        // publish reliable event (either direct Kafka with retries, or Outbox)
        publisher.publishCouponApplied(CouponAppliedEvent.of(r, cashback));

        return new ApplyCouponResponse(true, discount, payable, cashback, coupon.getUpdatedAt() == null ? "v1" : coupon.getUpdatedAt().toString());
    }

    private double computeCashback(Coupon c, double amount) {
        return Math.min(amount * c.getValue() / 100.0, c.getMaxDiscount() == null ? Double.MAX_VALUE : c.getMaxDiscount());
    }
}
