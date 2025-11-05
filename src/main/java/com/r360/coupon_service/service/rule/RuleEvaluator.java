package com.r360.coupon_service.service.rule;

import java.util.Map;

public interface RuleEvaluator {
    boolean evaluate(String expr, Map<String, Object> ctx);
}