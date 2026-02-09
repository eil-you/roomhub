package com.roomhub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

@AllArgsConstructor
@Getter
public enum ErrorCode implements BaseErrorCode {

    INVALID_INPUT_VALUE(400, "Invalid input value", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(500, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    METHOD_NOT_ALLOWED(405, "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),
    HANDLE_ACCESS_DENIED(403, "Access is denied", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    @NonNull
    private final HttpStatus httpStatus;

}
