package com.roomhub.exception;

import com.roomhub.model.ErrorCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class RoomHubException extends RuntimeException {
    private final int code;
    private final String message;
    private final Map<String, Object> parameters;
    private Exception rootCause;

    public RoomHubException(ErrorCode code, Map<String, Object> parameters) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.parameters = parameters;
    }

    public RoomHubException(ErrorCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.parameters = new HashMap<>();
    }

    public RoomHubException(ErrorCode code, Exception rootCause) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.parameters = new HashMap<>();
        this.rootCause = rootCause;
    }

    public RoomHubException(ErrorCode code, Map<String, Object> parameters, Exception rootCause) {
        this.code = code.getCode();
        this.message = code.getMessage();
        this.parameters = parameters;
        this.rootCause = rootCause;
    }
}
