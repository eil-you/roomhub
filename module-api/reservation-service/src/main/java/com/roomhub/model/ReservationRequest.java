package com.roomhub.model;

import com.roomhub.entity.Reservation;
import com.roomhub.exception.RoomHubException;
import com.roomhub.interfaces.ValidationCheck;
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
            throw new RoomHubException(ErrorCode.INVALID_INPUT_VALUE);
        }
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            throw new RoomHubException(ReservationErrorCode.INVALID_DATE);
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
