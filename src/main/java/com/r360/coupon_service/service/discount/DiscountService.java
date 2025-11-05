package com.r360.coupon_service.service.discount;

import com.r360.coupon_service.model.entity.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountEngineRegistry registry;

    /**
     * Compute the discount based on coupon type and context.
     */
    public double calculateDiscount(Coupon coupon, double cartValue) {
        if (coupon == null || coupon.getType() == null) return 0.0;

        var ctx = new DiscountContext(
                cartValue,
                coupon.getValue() != null ? coupon.getValue() : 0.0,
                coupon.getMaxDiscount(),
                coupon.getDomain(),
                coupon.getType()
        );

        var engine = registry.get(coupon.getType());
        double discount = engine.compute(ctx);

        // Guardrails: Ensure discount never exceeds cart value
        return Math.min(discount, cartValue);
    }
}
