package org.teya.ledgerservice.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Generic API response wrapper providing common status & error fields plus a data payload.
 * @param <T> payload type
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String status,
        String error,
        T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", null, data);
    }
    public static <T> ApiResponse<T> failure(String error) {
        return new ApiResponse<>("FAILED", error, null);
    }
}

