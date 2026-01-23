package com.roomhub.handler;

import com.roomhub.exception.RoomHubException;
import com.roomhub.model.CommonResponse;
import com.roomhub.model.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RoomHubExceptionHandler {

    /**
     * 비즈니스 로직 예외 처리
     */
    @ExceptionHandler(RoomHubException.class)
    public ResponseEntity<CommonResponse<Void>> handleRoomHubException(RoomHubException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();
        String location = getErrorLocation(e);
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (errorCode.getHttpStatus().is5xxServerError()) {
            log.error(
                    "[RoomHubException] SERVER_ERROR | URI: {} | Method: {} | Code: {} | Message: {} | Location: {} | Params: {}",
                    uri, method, e.getCode(), e.getMessage(), location, e.getParameters(), e);
        } else {
            log.warn(
                    "[RoomHubException] BUSINESS_WARN | URI: {} | Method: {} | Code: {} | Message: {} | Location: {} | Params: {}",
                    uri, method, e.getCode(), e.getMessage(), location, e.getParameters());
        }

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.fail(e.getCode(), e.getMessage()));
    }

    /**
     * @Valid 검증 실패 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.warn("[ValidationException] VALIDATION_FAIL | URI: {} | Method: {} | Errors: {} | Location: {}",
                request.getRequestURI(), request.getMethod(), errors, getErrorLocation(e));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.fail(400, "잘못된 입력값입니다.", errors));
    }

    /**
     * 잘못된 JSON 형식 또는 파싱 에러 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        log.warn("[ReadableException] INVALID_JSON | URI: {} | Method: {} | Message: {}",
                request.getRequestURI(), request.getMethod(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.fail(400, "요청 본문의 형식이 올바르지 않습니다."));
    }

    /**
     * 지원하지 않는 HTTP Method 호출 시 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<Void>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

        log.warn("[MethodException] METHOD_NOT_ALLOWED | URI: {} | Method: {} | Supported: {}",
                request.getRequestURI(), request.getMethod(), e.getSupportedHttpMethods());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(CommonResponse.fail(405, "지원하지 않는 요청 메서드입니다."));
    }

    /**
     * 시스템 최상위 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleGlobalException(Exception e, HttpServletRequest request) {
        log.error("[GlobalException] UNHANDLED_ERROR | URI: {} | Method: {} | Message: {} | Location: {}",
                request.getRequestURI(), request.getMethod(), e.getMessage(), getErrorLocation(e), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.fail(500, "서버 내부 오류가 발생했습니다."));
    }

    /**
     * 발생 위치 추출 (com.roomhub 패키지 기준)
     */
    private String getErrorLocation(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .filter(ste -> ste.getClassName().startsWith("com.roomhub"))
                .findFirst()
                .map(ste -> String.format("%s.%s(%s:%d)",
                        ste.getClassName(), ste.getMethodName(), ste.getFileName(), ste.getLineNumber()))
                .orElse("Unknown Location");
    }
}
