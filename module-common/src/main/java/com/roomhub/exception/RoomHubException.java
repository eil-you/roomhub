package com.roomhub.exception;

import com.roomhub.model.BaseErrorCode;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.Map;

@Getter
public class RoomHubException extends RuntimeException {
    @NonNull
    private final BaseErrorCode errorCode;
    private final Map<String, Object> parameters;

    public RoomHubException(@NonNull BaseErrorCode errorCode) {
        this(errorCode, Collections.emptyMap(), null);
    }

    public RoomHubException(@NonNull BaseErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = Collections.emptyMap();
    }

    public RoomHubException(@NonNull BaseErrorCode errorCode, Map<String, Object> parameters) {
        this(errorCode, parameters, null);
    }

    public RoomHubException(@NonNull BaseErrorCode errorCode, Throwable cause) {
        this(errorCode, Collections.emptyMap(), cause);
    }

    public RoomHubException(@NonNull BaseErrorCode errorCode, Map<String, Object> parameters, Throwable cause) {
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
