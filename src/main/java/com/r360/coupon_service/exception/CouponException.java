package com.r360.coupon_service.exception;

import lombok.Getter;

@Getter
public class CouponException extends RuntimeException {

    private final CouponErrors code;

    public CouponException(CouponErrors code, String message) {
        super(message);
        this.code = code;
    }

    public CouponException(CouponErrors code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getErrorCode() {
        return code.name();
    }
}
