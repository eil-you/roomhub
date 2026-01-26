package com.repository;

import com.roomhub.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 특정 사용자의 예약 내역 조회
    List<Reservation> findAllByUserId(Long userId);

    // 특정 방의 예약 내역 조회
    List<Reservation> findAllByRoomId(Long roomId);
}
