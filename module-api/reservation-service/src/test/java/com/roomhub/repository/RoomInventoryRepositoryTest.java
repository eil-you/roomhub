package com.roomhub.repository;

import com.roomhub.entity.RoomInventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RoomInventoryRepositoryTest {

    @Autowired
    private RoomInventoryRepository roomInventoryRepository;

    @Test
    @DisplayName("특정 방의 날짜 범위 재고 조회")
    void findAllByRoomIdAndDateBetween() {
        // given
        Long roomId = 1L;
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(2);

        roomInventoryRepository.save(RoomInventory.builder().roomId(roomId).date(start).stock(5).build());
        roomInventoryRepository.save(RoomInventory.builder().roomId(roomId).date(start.plusDays(1)).stock(5).build());
        roomInventoryRepository.save(RoomInventory.builder().roomId(roomId).date(end).stock(5).build());
        roomInventoryRepository.save(RoomInventory.builder().roomId(2L).date(start).stock(5).build()); // Other room

        // when
        List<RoomInventory> results = roomInventoryRepository.findAllByRoomIdAndDateBetween(roomId, start, end);

        // then
        assertEquals(3, results.size());
    }
}
