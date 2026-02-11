package com.couchping.exception;

import com.couchping.model.BaseErrorCode;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.Map;

@Getter
public class CouchPingException extends RuntimeException {
    @NonNull
    private final BaseErrorCode errorCode;
    private final Map<String, Object> parameters;

    public CouchPingException(@NonNull BaseErrorCode errorCode) {
        this(errorCode, Collections.emptyMap(), null);
    }

    public CouchPingException(@NonNull BaseErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = Collections.emptyMap();
    }

    public CouchPingException(@NonNull BaseErrorCode errorCode, Map<String, Object> parameters) {
        this(errorCode, parameters, null);
    }

    public CouchPingException(@NonNull BaseErrorCode errorCode, Throwable cause) {
        this(errorCode, Collections.emptyMap(), cause);
    }

    public CouchPingException(@NonNull BaseErrorCode errorCode, Map<String, Object> parameters, Throwable cause) {
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

