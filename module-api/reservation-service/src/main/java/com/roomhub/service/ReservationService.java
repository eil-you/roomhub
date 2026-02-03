package com.roomhub.service;

import com.roomhub.entity.Reservation;
import com.roomhub.entity.RoomInventory;
import com.roomhub.exception.RoomHubException;
import com.roomhub.model.ReservationErrorCode;
import com.roomhub.model.ReservationRequest;
import com.roomhub.model.ReservationStatus;
import com.roomhub.repository.ReservationRepository;
import com.roomhub.repository.RoomInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomInventoryRepository roomInventoryRepository;
    private final org.redisson.api.RedissonClient redissonClient;

    private static final String LOCK_KEY_PREFIX = "reservation_lock:";

    public void createReservation(ReservationRequest reservationRequest) {
        String lockKey = LOCK_KEY_PREFIX + reservationRequest.roomId();
        org.redisson.api.RLock lock = redissonClient.getLock(lockKey);

        try {
            // 락 획득 시도 (최대 5초 대기, 락 획득 후 10초간 유지)
            boolean available = lock.tryLock(5, 10, java.util.concurrent.TimeUnit.SECONDS);
            if (!available) {
                throw new RoomHubException(ReservationErrorCode.CONCURRENCY_ERROR);
            }

            // 실제 비즈니스 로직 수행
            processReservation(reservationRequest);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RoomHubException(ReservationErrorCode.SYSTEM_ERROR);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void processReservation(ReservationRequest reservationRequest) {
        // 1. 예약 기간 내의 모든 날짜별 재고 조회
        List<RoomInventory> roomInventories = roomInventoryRepository
                .findAllByRoomIdAndDateBetween(
                        reservationRequest.roomId(), reservationRequest.checkInDate(),
                        reservationRequest.checkOutDate().minusDays(1));// 체크아웃 당일은 제외

        // 2. 재고가 하나라도 부족하면 예약 불가 처리
        for (RoomInventory roomInventory : roomInventories) {
            if (roomInventory.getStock() <= 0) {
                throw new RoomHubException(ReservationErrorCode.ROOM_NOT_AVAILABLE);
            }
        }

        // 3. 재고 차감
        for (RoomInventory roomInventory : roomInventories) {
            roomInventory.decreaseStock();
        }

        // 4. 예약 저장
        reservationRepository.save(reservationRequest.toEntity());
    }

    // 예약 확정 (PENDING -> CONFIRMED)
    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomHubException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RoomHubException(ReservationErrorCode.INVALID_DATE);
        }

        reservation.updateStatus(ReservationStatus.CONFIRMED);
    }

    // 예약 취소
    @Transactional
    public void cancelReservation(Long reservationId) {
        // 1. 예약 내역 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RoomHubException(ReservationErrorCode.RESERVATION_NOT_FOUND));

        // 2. 이미 취소된 건인지 확인
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return;
        }

        // 3. 예약 기간 내의 재고 복구
        List<RoomInventory> roomInventories = roomInventoryRepository
                .findAllByRoomIdAndDateBetween(
                        reservation.getRoomId(), reservation.getCheckInDate(),
                        reservation.getCheckOutDate().minusDays(1));

        for (RoomInventory roomInventory : roomInventories) {
            roomInventory.increaseStock();
        }

        // 4. 예약 상태 변경
        reservation.updateStatus(ReservationStatus.CANCELLED);
    }

    // 사용자별 예약 조회
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findAllByUserId(userId);
    }
}