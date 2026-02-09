package com.roomhub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

@AllArgsConstructor
@Getter
public enum RoomErrorCode implements BaseErrorCode {
    ROOM_NOT_FOUND(3001, "Room not found", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS(3002, "You are not authorized to modify this room", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    @NonNull
    private final HttpStatus httpStatus;
}
