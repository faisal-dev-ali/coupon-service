package com.r360.coupon_service.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String code;         // Business or error code (e.g., COUPON_VALID, LIMIT_EXCEEDED)
    private String message;      // Human-readable message
    private T data;              // Generic payload (Coupon details, validation result, etc.)
    private Map<String, Object> meta; // Additional info (traceId, page info, etc.)
    private Instant timestamp;   // For traceability

    public static <T> ApiResponse<T> success(String code, String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(code)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> failure(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> failure(String code, String message, Map<String, Object> meta) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .meta(meta)
                .timestamp(Instant.now())
                .build();
    }
}
