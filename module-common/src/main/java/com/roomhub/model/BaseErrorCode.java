package com.roomhub.model;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

public interface BaseErrorCode {
    int getCode();

    String getMessage();

    @NonNull
    HttpStatus getHttpStatus();
}
