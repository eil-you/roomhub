package com.roomhub.exception;

import com.roomhub.model.ErrorCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class RoomHubException extends RuntimeException {
    private final ErrorCode errorCode;
    private final int code;
    private final String message;
    private final Map<String, Object> parameters;
    private Exception rootCause;

    public RoomHubException(ErrorCode errorCode, Map<String, Object> parameters) {
        this.errorCode = errorCode;
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.parameters = parameters;
    }

    public RoomHubException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.parameters = new HashMap<>();
    }

    public RoomHubException(ErrorCode errorCode, Exception rootCause) {
        this.errorCode = errorCode;
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.parameters = new HashMap<>();
        this.rootCause = rootCause;
    }

    public RoomHubException(ErrorCode errorCode, Map<String, Object> parameters, Exception rootCause) {
        this.errorCode = errorCode;
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.parameters = parameters;
        this.rootCause = rootCause;
    }
}
