package com.roomhub.handler;

import com.roomhub.exception.RoomHubException;
import com.roomhub.model.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class RoomHubExceptionHandler {

    @Order(1)
    @ExceptionHandler(RoomHubException.class)
    public CommonResponse<?> exceptionHandler(RoomHubException e) {
        Map<String, Object> parameters = e.getParameters();

        // 오류 발생 원인,
        if (e.getRootCause() != null) {
            String stackTrace = ExceptionUtils.getStackTrace(e);

            log.info("RoomHubException 발생! 파라미터: {} \n스택 트레이스: {}", parameters.values(), stackTrace);
        }

        return new CommonResponse(e.getCode(), e.getMessage(), null);
    }

    @Order(100)
    @ExceptionHandler(Exception.class)
    public CommonResponse<?> exceptionHandler(Exception e) {

        e.printStackTrace();

        int code = 500;
        String userMessage = "서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";

        return new CommonResponse(code, userMessage, null);
    }

}
