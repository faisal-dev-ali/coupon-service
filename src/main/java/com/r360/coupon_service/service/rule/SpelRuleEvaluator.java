package com.r360.coupon_service.service.rule;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpelRuleEvaluator implements RuleEvaluator {
    private final ExpressionParser parser = new SpelExpressionParser();
    @Override public boolean evaluate(String expr, Map<String, Object> ctx) {
        StandardEvaluationContext c = new StandardEvaluationContext();
        c.setVariables(ctx);
        Boolean pass = parser.parseExpression(expr).getValue(c, Boolean.class);
        return Boolean.TRUE.equals(pass);
    }
}