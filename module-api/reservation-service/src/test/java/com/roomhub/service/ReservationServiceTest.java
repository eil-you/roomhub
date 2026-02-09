package com.roomhub.service;

import com.roomhub.entity.Reservation;
import com.roomhub.exception.RoomHubException;
import com.roomhub.model.ReservationErrorCode;
import com.roomhub.model.ReservationRequest;
import com.roomhub.model.ReservationStatus;
import com.roomhub.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

        @Mock
        private ReservationRepository reservationRepository;

        @InjectMocks
        private ReservationService reservationService;

        @Test
        @DisplayName("예약 생성 성공 (매칭 기록)")
        void createReservation_Success() {
                // given
                ReservationRequest request = new ReservationRequest(1L, 1L, LocalDate.now(),
                                LocalDate.now().plusDays(1), 100000);

                // when
                reservationService.createReservation(request);

                // then
                verify(reservationRepository, times(1)).save(any(Reservation.class));
        }

        @Test
        @DisplayName("예약 취소 성공")
        void cancelReservation_Success() {
                // given
                Long reservationId = 1L;
                Reservation reservation = Reservation.builder()
                                .userId(1L)
                                .roomId(1L)
                                .checkInDate(LocalDate.now())
                                .checkOutDate(LocalDate.now().plusDays(1))
                                .totalPrice(100000)
                                .build();

                given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

                // when
                reservationService.cancelReservation(reservationId);

                // then
                assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        }

        @Test
        @DisplayName("특정 숙소의 모든 예약 취소 성공 (숙소 삭제 시)")
        void cancelAllByRoomId_Success() {
                // given
                Long roomId = 1L;
                Reservation res1 = Reservation.builder().roomId(roomId).build();
                res1.updateStatus(ReservationStatus.CONFIRMED);
                Reservation res2 = Reservation.builder().roomId(roomId).build();
                res2.updateStatus(ReservationStatus.CONFIRMED);

                given(reservationRepository.findAllByRoomId(roomId)).willReturn(List.of(res1, res2));

                // when
                reservationService.cancelAllByRoomId(roomId);

                // then
                assertEquals(ReservationStatus.CANCELLED, res1.getStatus());
                assertEquals(ReservationStatus.CANCELLED, res2.getStatus());
        }

        @Test
        @DisplayName("사용자별 예약 조회 성공")
        void getReservationsByUserId_Success() {
                // given
                Long userId = 1L;
                Reservation reservation = Reservation.builder()
                                .userId(userId)
                                .roomId(1L)
                                .checkInDate(LocalDate.now())
                                .checkOutDate(LocalDate.now().plusDays(1))
                                .totalPrice(100000)
                                .build();

                given(reservationRepository.findAllByUserId(userId)).willReturn(List.of(reservation));

                // when
                List<Reservation> results = reservationService.getReservationsByUserId(userId);

                // then
                assertEquals(1, results.size());
                assertEquals(userId, results.get(0).getUserId());
        }
}
