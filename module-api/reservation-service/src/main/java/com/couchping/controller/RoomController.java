package com.couchping.controller;

import com.couchping.entity.Room;
import com.couchping.model.RoomRequest;
import com.couchping.service.RoomService;
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
     * ?숈냼 ?깅줉
     */
    @PostMapping
    public ResponseEntity<Long> registerRoom(
            @RequestHeader("X-USER-ID") Long hostId, // ?꾩떆濡??ㅻ뜑?먯꽌 ?좎? ID 異붿텧 (?섏쨷???쒗걧由ы떚)
            @RequestBody RoomRequest request) {
        Long roomId = roomService.registerRoom(hostId, request);
        return ResponseEntity.ok(roomId);
    }

    /**
     * ?숈냼 ?섏젙
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
     * ?숈냼 ?쒖꽦???곹깭 蹂寃?(ON/OFF)
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
     * ?숈냼 ??젣 (Soft Delete)
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteRoom(
            @RequestHeader("X-USER-ID") Long hostId,
            @PathVariable Long roomId) {
        roomService.deleteRoom(hostId, roomId);
        return ResponseEntity.ok("Room deleted successfully (associated reservations cancelled)");
    }

    /**
     * ?숇컯 ?곸꽭 議고쉶
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoom(roomId));
    }

    /**
     * ?몄뒪?몃퀎 ?깅줉 ?숈냼 紐⑸줉 議고쉶
     */
    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<Room>> getRoomsByHost(@PathVariable Long hostId) {
        return ResponseEntity.ok(roomService.getRoomsByHost(hostId));
    }

    /**
     * ?꾩껜 ?쒖꽦 ?숈냼 紐⑸줉 議고쉶
     */
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllActiveRooms());
    }
}
