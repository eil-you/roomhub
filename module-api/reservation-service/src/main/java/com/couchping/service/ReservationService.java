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
     * ???고뒎 ??諛댁뎽 (嶺뚮씞?됭눧??筌먦끉???リ옇?▽빳?
     */
    @Transactional
    public void createReservation(ReservationRequest reservationRequest) {
        // 嶺?????繹먮냱諭????????諭踰????怨몃뮔????戮?츩??戮?뱺 ?リ옇?▽빳??濡ル츎 ????
        reservationRepository.save(reservationRequest.toEntity());
    }

    /**
     * ???고뒎 ?筌먦끉??(PENDING -> CONFIRMED)
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
     * ???고뒎 ???쳛??
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
     * ???????????고뒎 ?브퀗???
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
     * ?獄??????덇틬??嶺뚮ㅄ維獄??筌먦끉??????고뒎?????쳛??(???덇틬 ???????筌뤾쑵??
     */
    @Transactional
    public void cancelAllByRoomId(Long roomId) {
        List<Reservation> confirmedReservations = reservationRepository.findAllByRoomId(roomId).stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .toList();

        for (Reservation reservation : confirmedReservations) {
            reservation.updateStatus(ReservationStatus.CANCELLED);
            // TODO: ?롪퍓???筌뤾쑬?좈뇦?"???덇틬 ???節놁뿉??筌뤿굝由????쳛?? ???逾??꾩룇裕???β돦裕뉐퐲??熬곣뫗??
        }
    }
}

