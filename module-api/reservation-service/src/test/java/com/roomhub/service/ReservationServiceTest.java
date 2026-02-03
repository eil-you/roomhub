package com.roomhub.service;

import com.roomhub.entity.Reservation;
import com.roomhub.entity.RoomInventory;
import com.roomhub.exception.RoomHubException;
import com.roomhub.model.ReservationErrorCode;
import com.roomhub.model.ReservationRequest;
import com.roomhub.model.ReservationStatus;
import com.roomhub.repository.ReservationRepository;
import com.roomhub.repository.RoomInventoryRepository;
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

        @Mock
        private RoomInventoryRepository roomInventoryRepository;

        @InjectMocks
        private ReservationService reservationService;

        @Test
        @DisplayName("예약 생성 성공")
        void createReservation_Success() {
                // given
                ReservationRequest request = new ReservationRequest(1L, 1L, LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                100000);
                RoomInventory inventory1 = RoomInventory.builder().roomId(1L).date(LocalDate.now()).stock(1).build();
                RoomInventory inventory2 = RoomInventory.builder().roomId(1L).date(LocalDate.now().plusDays(1)).stock(2)
                                .build();

                given(roomInventoryRepository.findAllByRoomIdAndDateBetween(anyLong(), any(),
                                any()))
                                .willReturn(List.of(inventory1, inventory2));

                // when
                reservationService.createReservation(request);

                // then
                assertEquals(0, inventory1.getStock());
                assertEquals(1, inventory2.getStock());
                verify(reservationRepository, times(1)).save(any(Reservation.class));
        }

        @Test
        @DisplayName("예약 생성 실패 - 재고 부족")
        void createReservation_Fail_NoStock() {
                // given
                ReservationRequest request = new ReservationRequest(1L, 1L, LocalDate.now(),
                                LocalDate.now().plusDays(1),
                                100000);
                RoomInventory inventory1 = RoomInventory.builder().roomId(1L).date(LocalDate.now()).stock(1).build();
                RoomInventory inventory2 = RoomInventory.builder().roomId(1L).date(LocalDate.now().plusDays(1)).stock(0)
                                .build();

                given(roomInventoryRepository.findAllByRoomIdAndDateBetween(anyLong(), any(),
                                any()))
                                .willReturn(List.of(inventory1, inventory2));

                // when & then
                RoomHubException exception = assertThrows(RoomHubException.class,
                                () -> reservationService.createReservation(request));
                assertEquals(ReservationErrorCode.ROOM_NOT_AVAILABLE, exception.getErrorCode());
                verify(reservationRepository, never()).save(any(Reservation.class));
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

                RoomInventory inventory1 = RoomInventory.builder().roomId(1L).date(LocalDate.now()).stock(0).build();
                RoomInventory inventory2 = RoomInventory.builder().roomId(1L).date(LocalDate.now().plusDays(1)).stock(1)
                                .build();

                given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
                given(roomInventoryRepository.findAllByRoomIdAndDateBetween(anyLong(), any(),
                                any()))
                                .willReturn(List.of(inventory1, inventory2));

                // when
                reservationService.cancelReservation(reservationId);

                // then
                assertEquals(1, inventory1.getStock());
                assertEquals(2, inventory2.getStock());
                assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        }

        @Test
        @DisplayName("예약 취소 - 이미 취소된 경우")
        void cancelReservation_AlreadyCancelled() {
                // given
                Long reservationId = 1L;
                Reservation reservation = Reservation.builder()
                                .userId(1L)
                                .roomId(1L)
                                .checkInDate(LocalDate.now())
                                .checkOutDate(LocalDate.now().plusDays(1))
                                .totalPrice(100000)
                                .build();
                reservation.updateStatus(ReservationStatus.CANCELLED);

                given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

                // when
                reservationService.cancelReservation(reservationId);

                // then
                verify(roomInventoryRepository, never()).findAllByRoomIdAndDateBetween(anyLong(),
                                any(), any());
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
