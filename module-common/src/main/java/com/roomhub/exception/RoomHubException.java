package com.roomhub.exception;

import com.roomhub.model.ErrorCode;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class RoomHubException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> parameters;

    public RoomHubException(ErrorCode errorCode) {
        this(errorCode, Collections.emptyMap(), null);
    }

    public RoomHubException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = Collections.emptyMap();
    }

    public RoomHubException(ErrorCode errorCode, Map<String, Object> parameters) {
        this(errorCode, parameters, null);
    }

    public RoomHubException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, Collections.emptyMap(), cause);
    }

    public RoomHubException(ErrorCode errorCode, Map<String, Object> parameters, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.parameters = parameters != null ? parameters : Collections.emptyMap();
    }

    public int getCode() {
        return errorCode.getCode();
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
