package com.roomhub.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonResponse<T>(
        int code,
        String message,
        T content) {
    public static <T> CommonResponse<T> success(T content) {
        return new CommonResponse<>(200, "success", content);
    }

    public static <T> CommonResponse<T> success(int code, String message, T content) {
        return new CommonResponse<>(code, message, content);
    }

    public static <T> CommonResponse<T> fail(int code, String message) {
        return new CommonResponse<>(code, message, null);
    }

    public static <T> CommonResponse<T> fail(int code, String message, T content) {
        return new CommonResponse<>(code, message, content);
    }
}
