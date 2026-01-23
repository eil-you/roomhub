package com.roomhub.handler;

import com.roomhub.exception.RoomHubException;
import com.roomhub.model.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RoomHubExceptionHandler {

    // 1. 비즈니스 예외 처리 (커스텀 예외)
    @Order(1)
    @ExceptionHandler(RoomHubException.class)
    public ResponseEntity<CommonResponse<Void>> handleRoomHubException(RoomHubException e) {
        Map<String, Object> parameters = e.getParameters();

        if (e.getRootCause() != null) {
            String stackTrace = ExceptionUtils.getStackTrace(e.getRootCause());
            log.info("RoomHubException 발생! 파라미터: {} \n스택 트레이스: {}", parameters.values(), stackTrace);
        }

        HttpStatus status = e.getErrorCode().getHttpStatus();
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(new CommonResponse<>(e.getCode(), e.getMessage(), null));
    }

    // 2. @Valid 검증 실패 처리
    @Order(2)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.warn("Validation Failed: {}", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CommonResponse<>(400, "입력값이 유효하지 않습니다.", errors));
    }

    // 3. 기타 예상치 못한 예외 처리
    @Order(100)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception e) {
        log.error("Unhandled Exception occurred: ", e);

        int code = 500;
        String userMessage = "서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CommonResponse<>(code, userMessage, null));
    }
}
