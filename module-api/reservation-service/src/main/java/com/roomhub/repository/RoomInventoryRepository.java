package com.roomhub.repository;

import com.roomhub.entity.RoomInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomInventoryRepository extends JpaRepository<RoomInventory, Long> {

    // 특정 방의 특정 날짜 범위 재고 조회
    List<RoomInventory> findAllByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);
}
