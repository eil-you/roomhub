package com.roomhub.exception;

import com.roomhub.model.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class RoomHubException extends RuntimeException {
    private final int code;
    private final String message;
    private Map<String, Object> parameters;
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
}
