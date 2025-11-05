package com.r360.coupon_service.controller;

import com.r360.coupon_service.exception.CouponException;
import com.r360.coupon_service.model.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponException.class)
    public ResponseEntity<ApiResponse<?>> handleCouponException(CouponException ex) {
        HttpStatus status = switch (ex.getCode()) {
            case INVALID_CODE -> HttpStatus.NOT_FOUND;
            case NOT_ACTIVE -> HttpStatus.GONE;
            case EXPIRED -> HttpStatus.GONE;
            case MIN_CART -> HttpStatus.BAD_REQUEST;
            case LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
            case FRAUD_BLOCKED -> HttpStatus.FORBIDDEN;
            case PAYMENT_NOT_ELIGIBLE, SEGMENT_NOT_ELIGIBLE -> HttpStatus.BAD_REQUEST;
            case DUPLICATE_ORDER, IDEMPOTENT_REPLAY -> HttpStatus.CONFLICT;
            default -> HttpStatus.UNPROCESSABLE_ENTITY;
        };

        var response = ApiResponse.failure(
                ex.getErrorCode(),
                ex.getMessage()
        );

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex) {
        ex.printStackTrace();
        var response = ApiResponse.failure("INTERNAL_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
