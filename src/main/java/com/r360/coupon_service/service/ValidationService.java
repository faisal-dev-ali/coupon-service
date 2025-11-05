package com.r360.coupon_service.service;

import com.r360.coupon_service.exception.CouponErrors;
import com.r360.coupon_service.exception.CouponException;
import com.r360.coupon_service.model.dto.ValidationRequest;
import com.r360.coupon_service.model.dto.ValidationResult;
import com.r360.coupon_service.model.entity.Coupon;
import com.r360.coupon_service.repository.CouponRepository;
import com.r360.coupon_service.repository.CouponRuleRepository;
import com.r360.coupon_service.service.discount.DiscountContext;
import com.r360.coupon_service.service.discount.DiscountEngineRegistry;
import com.r360.coupon_service.service.rule.RuleEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

// service/ValidationService.java
@Service
@RequiredArgsConstructor
public class ValidationService {
    private final CouponRepository couponRepo;
    private final CouponRuleRepository ruleRepo;
    private final RuleEvaluator ruleEvaluator;
    private final DiscountEngineRegistry discountRegistry;

    @Transactional(readOnly = true)
    public ValidationResult validate(ValidationRequest req) {
        Coupon c = couponRepo.findActiveByCode(req.code())
                .orElseThrow(() -> new CouponException(CouponErrors.INVALID_CODE, "Invalid coupon"));

        // window & domain checks
        var now = LocalDateTime.now();
        if (Boolean.FALSE.equals(c.getActive()) || now.isBefore(c.getStartAt()) || now.isAfter(c.getEndAt()))
            throw new CouponException(CouponErrors.EXPIRED, "Coupon not active in window");
        if (!c.getDomain().equalsIgnoreCase(req.domain()))
            throw new CouponException(CouponErrors.DOMAIN_MISMATCH, "Not valid for this domain");
        if (c.getMinCartValue() != null && req.amount() < c.getMinCartValue())
            throw new CouponException(CouponErrors.MIN_CART, "Minimum cart not met");

        // rule evaluation (payment, bin, channel, merchant/category, segments…)
        var rules = ruleRepo.findByCouponIdOrdered(c.getId());
        Map<String, Object> ctx = buildCtx(c, req);
        for (var r : rules) {
            if (!ruleEvaluator.evaluate(r.getRuleExpr(), ctx)) {
                throw new CouponException(CouponErrors.PAYMENT_NOT_ELIGIBLE, "Rule failed");
            }
        }

        // discount math
        var engine = discountRegistry.get(c.getType());
        double discount = engine.compute(new DiscountContext(
                req.amount(),             // cart value
                c.getValue(),        // coupon value
                c.getMaxDiscount(),  // optional cap
                c.getDomain(),       // FLIGHT / HOTEL etc.
                c.getType()          // PERCENT / FLAT / CASHBACK
        ));

        double payable = Math.max(0, req.amount() - discount);

        return new ValidationResult(true, "Applicable", discount, payable,
                Map.of("code", c.getCode(), "stackable", Boolean.TRUE.equals(c.getStackable())));
    }

    private Map<String, Object> buildCtx(Coupon c, ValidationRequest req) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("coupon", c.getCode());
        ctx.put("domain", req.domain());
        ctx.put("amount", req.amount());
        ctx.put("channel", req.channel());
        ctx.put("payment", Map.of(
                "mode", req.payment() == null ? null : req.payment().mode(),
                "bank", req.payment() == null ? null : req.payment().bank(),
                "bin",  req.payment() == null ? null : req.payment().bin()
        ));
        ctx.put("userId", req.userId());
        // you can enrich with user’s historical stats from a cache if needed
        return ctx;
    }
}
