package com.couchping.model;

import com.couchping.entity.Reservation;
import com.couchping.exception.CouchPingException;
import com.couchping.interfaces.ValidationCheck;
import java.time.LocalDate;

public record ReservationRequest(
        Long userId,
        Long roomId,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        int totalPrice) implements ValidationCheck {

    @Override
    public void check() {
        if (userId == null || roomId == null) {
            throw new CouchPingException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            throw new CouchPingException(ReservationErrorCode.INVALID_DATE);
        }

    }

    public Reservation toEntity() {
        return Reservation.builder()
                .userId(userId)
                .roomId(roomId)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .totalPrice(totalPrice)
                .build();
    }

}

