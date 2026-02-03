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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

        @Mock
        private org.redisson.api.RedissonClient redissonClient;

        @Mock
        private org.redisson.api.RLock lock;

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

                given(redissonClient.getLock(anyString())).willReturn(lock);
                try {
                        given(lock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }

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

                given(redissonClient.getLock(anyString())).willReturn(lock);
                try {
                        given(lock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }

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

        @Test
        @DisplayName("동시성 테스트 - 100개의 요청이 동시에 올 때 1개만 성공해야 함")
        void createReservation_Concurrency_Test() throws InterruptedException {
                // given
                int threadCount = 100;
                ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
                CountDownLatch latch = new CountDownLatch(threadCount);

                ReservationRequest request = new ReservationRequest(1L, 1L, LocalDate.now(),
                                LocalDate.now().plusDays(1), 100000);

                // 1. 락 설정: 첫 번째 시도만 true, 나머지는 false 반환하도록 설정
                given(redissonClient.getLock(anyString())).willReturn(lock);
                try {
                        given(lock.tryLock(anyLong(), anyLong(), any()))
                                        .willReturn(true) // 1등 성공
                                        .willReturn(false); // 나머지는 모두 실패
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }

                // 2. 재고 설정 (방이 1개 있는 상태)
                RoomInventory inventory = RoomInventory.builder().roomId(1L).date(LocalDate.now()).stock(1).build();
                given(roomInventoryRepository.findAllByRoomIdAndDateBetween(anyLong(), any(), any()))
                                .willReturn(List.of(inventory));

                AtomicInteger successCount = new AtomicInteger();
                AtomicInteger failCount = new AtomicInteger();

                // when
                for (int i = 0; i < threadCount; i++) {
                        executorService.submit(() -> {
                                try {
                                        reservationService.createReservation(request);
                                        successCount.incrementAndGet();
                                } catch (RoomHubException e) {
                                        if (e.getErrorCode() == ReservationErrorCode.CONCURRENCY_ERROR) {
                                                failCount.incrementAndGet();
                                        }
                                } finally {
                                        latch.countDown();
                                }
                        });
                }
                latch.await();

                // then
                assertEquals(1, successCount.get(), "단 한 명의 사용자만 예약에 성공해야 함");
                assertEquals(threadCount - 1, failCount.get(), "나머지 99명은 락 획득 실패(Concurrency Error)가 발생해야 함");
                verify(reservationRepository, times(1)).save(any());
        }
}
