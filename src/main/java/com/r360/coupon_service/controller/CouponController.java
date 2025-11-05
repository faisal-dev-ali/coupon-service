package com.r360.coupon_service.controller;

import com.r360.coupon_service.model.api.ApiResponse;
import com.r360.coupon_service.model.dto.ApplyCouponRequest;
import com.r360.coupon_service.model.dto.ApplyCouponResponse;
import com.r360.coupon_service.model.dto.CouponResponse;
import com.r360.coupon_service.model.dto.CreateCouponRequest;
import com.r360.coupon_service.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // ------------------------------------------------------------------------
    // 🧩 Create Coupon
    // ------------------------------------------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(
            @Valid @RequestBody CreateCouponRequest request) {
        log.info("Creating coupon with code={}", request.getCode());
        CouponResponse created = couponService.createCoupon(request);
        return ResponseEntity.ok(ApiResponse.success("COUPON_CREATED", "Coupon created successfully", created));
    }

    // ------------------------------------------------------------------------
    // 🧩 List All Coupons (optionally by domain & active flag)
    // ------------------------------------------------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getCoupons(
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) Boolean active) {
        log.debug("Listing coupons - domain={}, active={}", domain, active);
        List<CouponResponse> list = couponService.getAllCoupons(domain, active);
        return ResponseEntity.ok(ApiResponse.success("COUPON_LIST", "Coupons fetched successfully", list));
    }

    // ------------------------------------------------------------------------
    // 🧩 Get Coupon by Code
    // ------------------------------------------------------------------------
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<CouponResponse>> getCoupon(@PathVariable String code) {
        log.debug("Fetching coupon {}", code);
        CouponResponse coupon = couponService.getCouponByCode(code);
        return ResponseEntity.ok(ApiResponse.success("COUPON_DETAIL", "Coupon fetched successfully", coupon));
    }

    // ------------------------------------------------------------------------
    // 🧩 Validate Coupon (dry-run, no persistence)
    // ------------------------------------------------------------------------
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ApplyCouponResponse>> validateCoupon(
            @Valid @RequestBody ApplyCouponRequest request) {
        log.info("Validating coupon {} for user {}", request.code(), request.userId());
        ApplyCouponResponse response = couponService.validateCoupon(request);
        return ResponseEntity.ok(ApiResponse.success("COUPON_VALID", "Coupon is valid", response));
    }

    // ------------------------------------------------------------------------
    // 🧩 Apply Coupon (increments usage + enforces limits)
    // ------------------------------------------------------------------------
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<ApplyCouponResponse>> applyCoupon(
            @Valid @RequestBody ApplyCouponRequest request) {
        log.info("Applying coupon {} for user {}", request.code(), request.userId());
        ApplyCouponResponse response = couponService.applyCoupon(request);
        return ResponseEntity.ok(ApiResponse.success("COUPON_APPLIED", "Coupon applied successfully", response));
    }

    // ------------------------------------------------------------------------
    // 🧩 Update Coupon
    // ------------------------------------------------------------------------
    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(
            @PathVariable String code,
            @Valid @RequestBody CreateCouponRequest request) {
        log.info("Updating coupon {}", code);
        CouponResponse updated = couponService.updateCoupon(code, request);
        return ResponseEntity.ok(ApiResponse.success("COUPON_UPDATED", "Coupon updated successfully", updated));
    }

    // ------------------------------------------------------------------------
    // 🧩 Toggle Active/Inactive
    // ------------------------------------------------------------------------
    @PatchMapping("/{code}/status")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @PathVariable String code,
            @RequestParam boolean active) {
        log.info("Toggling coupon {} to {}", code, active ? "active" : "inactive");
        couponService.toggleStatus(code, active);
        return ResponseEntity.ok(ApiResponse.success("COUPON_STATUS_UPDATED", "Coupon status updated", null));
    }

    // ------------------------------------------------------------------------
    // 🧩 Delete Coupon
    // ------------------------------------------------------------------------
    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable String code) {
        log.warn("Deleting coupon {}", code);
        couponService.deleteCoupon(code);
        return ResponseEntity.ok(ApiResponse.success("COUPON_DELETED", "Coupon deleted successfully", null));
    }
}
