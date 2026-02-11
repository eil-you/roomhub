package com.couchping.repository;

import com.couchping.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHostId(Long hostId);

    List<Room> findAllByIsActiveTrueAndIsDeletedFalse();
}
