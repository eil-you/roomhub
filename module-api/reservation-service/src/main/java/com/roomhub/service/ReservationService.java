package com.roomhub.service;

import com.roomhub.entity.Reservation;
import com.roomhub.exception.RoomHubException;
import com.roomhub.model.ReservationErrorCode;
import com.roomhub.model.ReservationRequest;
import com.roomhub.model.ReservationStatus;
import com.roomhub.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * 예약 생성 (매칭 확정 기록)
     */
    @Transactional
    public void createReservation(ReservationRequest reservationRequest) {
        // 채팅 등을 통해 합의된 내용을 시스템에 기록하는 역할
        reservationRepository.save(reservationRequest.toEntity());
    }

    /**
     * 예약 확정 (PENDING -> CONFIRMED)
     */
    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomHubException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RoomHubException(ReservationErrorCode.INVALID_DATE);
        }

        reservation.updateStatus(ReservationStatus.CONFIRMED);
    }

    /**
     * 예약 취소
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomHubException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return;
        }

        reservation.updateStatus(ReservationStatus.CANCELLED);
    }

    /**
     * 사용자별 예약 조회
     */
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAllByRoomId(Long roomId) {
        return reservationRepository.findAllByRoomId(roomId);
    }

    /**
     * 특정 숙소의 모든 확정된 예약을 취소 (숙소 삭제 시 호출)
     */
    @Transactional
    public void cancelAllByRoomId(Long roomId) {
        List<Reservation> confirmedReservations = reservationRepository.findAllByRoomId(roomId).stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .toList();

        for (Reservation reservation : confirmedReservations) {
            reservation.updateStatus(ReservationStatus.CANCELLED);
            // TODO: 게스트에게 "숙소 삭제로 인한 취소" 알림 발송 로직 필요
        }
    }
}