package com.r360.coupon_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.r360.coupon_service.exception.CouponErrors;
import com.r360.coupon_service.exception.CouponException;
import com.r360.coupon_service.model.dto.ApplyCouponRequest;
import com.r360.coupon_service.model.dto.ApplyCouponResponse;
import com.r360.coupon_service.model.dto.CouponResponse;
import com.r360.coupon_service.model.dto.CreateCouponRequest;
import com.r360.coupon_service.model.entity.Coupon;
import com.r360.coupon_service.repository.CouponRepository;
import com.r360.coupon_service.service.discount.DiscountService;
import com.r360.coupon_service.service.limits.LimitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final DiscountService discountService;
    private final LimitService limitService;
    private final ObjectMapper objectMapper;

    // ------------------------------------------------------------------------
    // 🧩 CREATE
    // ------------------------------------------------------------------------
    @Transactional
    public CouponResponse createCoupon(CreateCouponRequest req) {
        log.info("Creating new coupon: {}", objectToJsonString(req));

        // Prevent duplicate codes
        couponRepository.findByCode(req.getCode()).ifPresent(c -> {
            throw new CouponException(CouponErrors.DUPLICATE_ORDER, "Coupon code already exists");
        });

        Coupon coupon = new Coupon();
        coupon.setCode(req.getCode());
        coupon.setTitle(req.getTitle());
        coupon.setDescription(req.getDescription());
        coupon.setDomain(req.getDomain());
        coupon.setType(req.getType());
        coupon.setValue(req.getValue());
        coupon.setMaxDiscount(req.getMaxDiscount());
        coupon.setMinCartValue(req.getMinCartValue());
        coupon.setStackable(req.getStackable());
        coupon.setStartAt(req.getStartAt());
        coupon.setEndAt(req.getEndAt());
        coupon.setActive(true);
        coupon.setCreatedAt(LocalDateTime.now());

        Coupon saved = couponRepository.save(coupon);
        log.info("Coupon {} created successfully", saved.getCode());
        return CouponResponse.from(saved);
    }

    // ------------------------------------------------------------------------
    // 🧩 LIST / SEARCH
    // ------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons(String domain, Boolean active) {
        log.debug("Fetching coupons - domain={}, active={}", domain, active);

        List<Coupon> coupons = (domain != null && active != null)
                ? couponRepository.findByDomainAndActive(domain, active)
                : couponRepository.findAll();

        return coupons.stream().map(CouponResponse::from).toList();
    }

    // ------------------------------------------------------------------------
    // 🧩 FETCH SINGLE COUPON
    // ------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public CouponResponse getCouponByCode(String code) {
        log.debug("Fetching coupon by code: {}", code);

        return couponRepository.findByCode(code)
                .map(CouponResponse::from)
                .orElseThrow(() -> new CouponException(CouponErrors.INVALID_CODE, "Coupon not found"));
    }

    // ------------------------------------------------------------------------
    // 🧩 VALIDATE COUPON (no persistence)
    // ------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public ApplyCouponResponse validateCoupon(ApplyCouponRequest req) {
        log.info("Validating coupon {} for user {}", req.code(), req.userId());

        Coupon coupon = couponRepository.findByCode(req.code())
                .orElseThrow(() -> new CouponException(CouponErrors.INVALID_CODE, "Coupon not found"));

        validateEligibility(coupon, req);

        limitService.previewLimits(coupon, req.userId());

        double discount = discountService.calculateDiscount(coupon, req.amount());
        double payable = req.amount() - discount;
        double cashback = coupon.getType().equalsIgnoreCase("CASHBACK") ? discount : 0.0;

        return new ApplyCouponResponse(
                true,
                discount,
                payable,
                cashback,
                coupon.getCode() // or version hash for audits
        );
    }

    // ------------------------------------------------------------------------
    // 🧩 APPLY COUPON (validates + enforces usage limits)
    // ------------------------------------------------------------------------
    @Transactional
    public ApplyCouponResponse applyCoupon(ApplyCouponRequest req) {
        log.info("Applying coupon {} for user {}", req.code(), req.userId());

        ApplyCouponResponse response = validateCoupon(req);

        Coupon coupon = couponRepository.findByCode(req.code())
                .orElseThrow(() -> new CouponException(CouponErrors.INVALID_CODE, "Coupon not found"));

        limitService.assertWithinLimits(coupon, req.userId(), req.deviceId());
        log.info("Coupon {} applied successfully for user {}", coupon.getCode(), req.userId());

        return response;
    }

    // ------------------------------------------------------------------------
    // 🧩 UPDATE COUPON
    // ------------------------------------------------------------------------
    @Transactional
    public CouponResponse updateCoupon(String code, CreateCouponRequest req) {
        log.info("Updating coupon: {}", code);

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new CouponException(CouponErrors.INVALID_CODE, "Coupon not found"));

        coupon.setTitle(req.getTitle());
        coupon.setDescription(req.getDescription());
        coupon.setValue(req.getValue());
        coupon.setMaxDiscount(req.getMaxDiscount());
        coupon.setMinCartValue(req.getMinCartValue());
        coupon.setEndAt(req.getEndAt());
        coupon.setUpdatedAt(LocalDateTime.now());

        couponRepository.save(coupon);
        log.info("Coupon {} updated successfully", code);

        return CouponResponse.from(coupon);
    }

    // ------------------------------------------------------------------------
    // 🧩 TOGGLE ACTIVE / INACTIVE
    // ------------------------------------------------------------------------
    @Transactional
    public void toggleStatus(String code, boolean active) {
        log.info("Toggling coupon status: {} -> {}", code, active);

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new CouponException(CouponErrors.INVALID_CODE, "Coupon not found"));

        coupon.setActive(active);
        coupon.setUpdatedAt(LocalDateTime.now());

        couponRepository.save(coupon);
        log.info("Coupon {} is now {}", code, active ? "active" : "inactive");
    }

    // ------------------------------------------------------------------------
    // 🧩 DELETE COUPON
    // ------------------------------------------------------------------------
    @Transactional
    public void deleteCoupon(String code) {
        log.warn("Deleting coupon {}", code);

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new CouponException(CouponErrors.INVALID_CODE, "Coupon not found"));

        couponRepository.delete(coupon);
        log.info("Coupon {} deleted successfully", code);
    }

    // ------------------------------------------------------------------------
    // 🧩 PRIVATE: VALIDATION LOGIC
    // ------------------------------------------------------------------------
    private void validateEligibility(Coupon coupon, ApplyCouponRequest req) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        System.out.println(now);

        if (!coupon.getActive()) {
            throw new CouponException(CouponErrors.NOT_ACTIVE, "Coupon is inactive");
        }

        if (now.isBefore(coupon.getStartAt()) || now.isAfter(coupon.getEndAt())) {
            throw new CouponException(CouponErrors.EXPIRED, "Coupon validity has expired");
        }

        if (req.amount() < coupon.getMinCartValue()) {
            throw new CouponException(CouponErrors.MIN_CART, "Minimum cart value not met");
        }

        if (coupon.getDomain() != null &&
                req.domain() != null &&
                !coupon.getDomain().equalsIgnoreCase(req.domain())) {
            throw new CouponException(CouponErrors.DOMAIN_MISMATCH, "Coupon not applicable to this domain");
        }
    }

    private String objectToJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            log.info("Error in parsing");
            return "{}";
        }
    }
}
