package com.roomhub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

@AllArgsConstructor
@Getter
public enum ReservationErrorCode implements BaseErrorCode {

    ROOM_NOT_AVAILABLE(2001, "Room not available for selected dates", HttpStatus.BAD_REQUEST),
    RESERVATION_NOT_FOUND(2002, "Reservation not found", HttpStatus.NOT_FOUND),
    INVALID_DATE(2003, "Invalid date", HttpStatus.BAD_REQUEST),
    CONCURRENCY_ERROR(2004, "Try again in a few moments", HttpStatus.CONFLICT),
    SYSTEM_ERROR(2005, "System error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    @NonNull
    private final HttpStatus httpStatus;

}
