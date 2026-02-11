package com.couchping.service;

import com.couchping.entity.Reservation;
import com.couchping.exception.CouchPingException;
import com.couchping.model.ReservationErrorCode;
import com.couchping.model.ReservationRequest;
import com.couchping.model.ReservationStatus;
import com.couchping.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * 숙소 예약 생성
     */
    @Transactional
    public void createReservation(ReservationRequest reservationRequest) {
        // 예약 요청 정보를 바탕으로 예약 엔티티 생성 및 저장
        reservationRepository.save(reservationRequest.toEntity());
    }

    /**
     * 숙소 예약 확정 (PENDING -> CONFIRMED)
     */
    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CouchPingException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new CouchPingException(ReservationErrorCode.INVALID_DATE);
        }

        reservation.updateStatus(ReservationStatus.CONFIRMED);
    }

    /**
     * 숙소 예약 취소
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CouchPingException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return;
        }

        reservation.updateStatus(ReservationStatus.CANCELLED);
    }

    /**
     * 유저별 예약 내역 조회
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
     * 특정 숙소의 모든 확정된 예약 취소 (숙소 삭제 시 호출)
     */
    @Transactional
    public void cancelAllByRoomId(Long roomId) {
        List<Reservation> confirmedReservations = reservationRepository.findAllByRoomId(roomId).stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .toList();

        for (Reservation reservation : confirmedReservations) {
            reservation.updateStatus(ReservationStatus.CANCELLED);
            // TODO: 알림 발송 (숙소 삭제로 인해 예약이 취소되었음을 게스트에게 알림)
        }
    }
}
