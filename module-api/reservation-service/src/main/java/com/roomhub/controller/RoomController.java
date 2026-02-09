package com.roomhub.controller;

import com.roomhub.entity.Room;
import com.roomhub.model.RoomRequest;
import com.roomhub.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * 숙소 등록
     */
    @PostMapping
    public ResponseEntity<Long> registerRoom(
            @RequestHeader("X-USER-ID") Long hostId, // 임시로 헤더에서 유저 ID 추출 (나중엔 시큐리티)
            @RequestBody RoomRequest request) {
        Long roomId = roomService.registerRoom(hostId, request);
        return ResponseEntity.ok(roomId);
    }

    /**
     * 숙소 수정
     */
    @PutMapping("/{roomId}")
    public ResponseEntity<String> updateRoom(
            @RequestHeader("X-USER-ID") Long hostId,
            @PathVariable Long roomId,
            @RequestBody RoomRequest request) {
        roomService.updateRoom(hostId, roomId, request);
        return ResponseEntity.ok("Room updated successfully");
    }

    /**
     * 숙소 활성화 상태 변경 (ON/OFF)
     */
    @PatchMapping("/{roomId}/status")
    public ResponseEntity<String> toggleStatus(
            @RequestHeader("X-USER-ID") Long hostId,
            @PathVariable Long roomId,
            @RequestParam boolean isActive) {
        roomService.toggleActiveStatus(hostId, roomId, isActive);
        return ResponseEntity.ok("Room status updated successfully");
    }

    /**
     * 숙소 삭제 (Soft Delete)
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteRoom(
            @RequestHeader("X-USER-ID") Long hostId,
            @PathVariable Long roomId) {
        roomService.deleteRoom(hostId, roomId);
        return ResponseEntity.ok("Room deleted successfully (associated reservations cancelled)");
    }

    /**
     * 숙박 상세 조회
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoom(roomId));
    }

    /**
     * 호스트별 등록 숙소 목록 조회
     */
    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<Room>> getRoomsByHost(@PathVariable Long hostId) {
        return ResponseEntity.ok(roomService.getRoomsByHost(hostId));
    }

    /**
     * 전체 활성 숙소 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllActiveRooms());
    }
}
