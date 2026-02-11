package com.couchping.handler;

import com.couchping.exception.CouchPingException;
import com.couchping.model.CommonResponse;
import com.couchping.model.BaseErrorCode;
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
public class CouchPingExceptionHandler {

    /**
     * 鍮꾩쫰?덉뒪 濡쒖쭅 ?덉쇅 泥섎━
     */
    @ExceptionHandler(CouchPingException.class)
    public ResponseEntity<CommonResponse<Void>> handleCouchPingException(CouchPingException e,
            HttpServletRequest request) {
        BaseErrorCode errorCode = e.getErrorCode();
        String location = getErrorLocation(e);
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (errorCode.getHttpStatus().is5xxServerError()) {
            log.error(
                    "[CouchPingException] SERVER_ERROR | URI: {} | Method: {} | Code: {} | Message: {} | Location: {} | Params: {}",
                    uri, method, e.getCode(), e.getMessage(), location, e.getParameters(), e);
        } else {
            log.warn(
                    "[CouchPingException] BUSINESS_WARN | URI: {} | Method: {} | Code: {} | Message: {} | Location: {} | Params: {}",
                    uri, method, e.getCode(), e.getMessage(), location, e.getParameters());
        }

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.fail(e.getCode(), e.getMessage()));
    }

    /**
     * @Valid 寃???ㅽ뙣 ?덉쇅 泥섎━
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        log.warn("[ValidationException] VALIDATION_FAIL | URI: {} | Method: {} | Errors: {} | Location: {}",
                request.getRequestURI(), request.getMethod(), errors, getErrorLocation(e));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.fail(400, "?섎せ???낅젰媛믪엯?덈떎.", errors));
    }

    /**
     * ?섎せ??JSON ?뺤떇 ?먮뒗 ?뚯떛 ?먮윭 泥섎━
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        log.warn("[ReadableException] INVALID_JSON | URI: {} | Method: {} | Message: {}",
                request.getRequestURI(), request.getMethod(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.fail(400, "?붿껌 蹂몃Ц???뺤떇???щ컮瑜댁? ?딆뒿?덈떎."));
    }

    /**
     * 吏?먰븯吏 ?딅뒗 HTTP Method ?몄텧 ??泥섎━
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<Void>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

        log.warn("[MethodException] METHOD_NOT_ALLOWED | URI: {} | Method: {} | Supported: {}",
                request.getRequestURI(), request.getMethod(), e.getSupportedHttpMethods());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(CommonResponse.fail(405, "吏?먰븯吏 ?딅뒗 ?붿껌 硫붿꽌?쒖엯?덈떎."));
    }

    /**
     * ?쒖뒪??理쒖긽???덉쇅 泥섎━
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleGlobalException(Exception e, HttpServletRequest request) {
        log.error("[GlobalException] UNHANDLED_ERROR | URI: {} | Method: {} | Message: {} | Location: {}",
                request.getRequestURI(), request.getMethod(), e.getMessage(), getErrorLocation(e), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.fail(500, "?쒕쾭 ?대? ?ㅻ쪟媛 諛쒖깮?덉뒿?덈떎."));
    }

    /**
     * 諛쒖깮 ?꾩튂 異붿텧 (com.couchping ?⑦궎吏 湲곗?)
     */
    private String getErrorLocation(Exception e) {
        return Arrays.stream(e.getStackTrace())
                .filter(ste -> ste.getClassName().startsWith("com.couchping"))
                .findFirst()
                .map(ste -> String.format("%s.%s(%s:%d)",
                        ste.getClassName(), ste.getMethodName(), ste.getFileName(),
                        ste.getLineNumber()))
                .orElse("Unknown Location");
    }
}


