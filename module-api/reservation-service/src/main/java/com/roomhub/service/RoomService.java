package com.roomhub.service;

import com.roomhub.entity.Room;
import com.roomhub.exception.RoomHubException;
import com.roomhub.model.RoomErrorCode;
import com.roomhub.model.RoomRequest;
import com.roomhub.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationService reservationService;

    /**
     * 1. 숙소 등록
     */
    @Transactional
    public Long registerRoom(Long hostId, RoomRequest request) {
        Room room = request.toEntity(hostId);
        return roomRepository.save(room).getId();
    }

    /**
     * 2. 숙소 정보 수정
     */
    @Transactional
    public void updateRoom(Long hostId, Long roomId, RoomRequest request) {
        Room room = findRoomAndCheckOwnership(hostId, roomId);

        room.update(
                request.title(),
                request.description(),
                request.imageUrl(),
                request.location(),
                request.price(),
                request.capacity(),
                request.initialQuestion(),
                request.roomType(),
                request.preferredGender(),
                request.amenities());
    }

    /**
     * 3. 숙소 노출 상태 토글 (ON/OFF)
     */
    @Transactional
    public void toggleActiveStatus(Long hostId, Long roomId, boolean isActive) {
        Room room = findRoomAndCheckOwnership(hostId, roomId);
        room.updateStatus(isActive);
    }

    /**
     * 4. 숙소 삭제 전 확정된 예약 수 확인
     */
    @Transactional(readOnly = true)
    public int getConfirmedReservationCount(Long roomId) {
        // 예약 서비스에서 확정된 예약 목록을 가져와 개수를 반환 (ReservationService.cancelAllByRoomId 참고)
        // 이 부분은 컨트롤러에서 삭제 전 경고창을 띄우기 위해 사용될 수 있습니다.
        return (int) reservationService.findAllByRoomId(roomId).stream()
                .filter(r -> r.getStatus().name().equals("CONFIRMED"))
                .count();
    }

    /**
     * 5. 숙소 삭제 (Soft Delete)
     * 확정된 예약이 있다면 모두 취소 처리하고 알림을 보냅니다.
     */
    @Transactional
    public void deleteRoom(Long hostId, Long roomId) {
        Room room = findRoomAndCheckOwnership(hostId, roomId);

        // 1. 해당 숙소의 모든 확정된 예약 취소 (ReservationService에 위임)
        reservationService.cancelAllByRoomId(roomId);

        // 2. 숙소 삭제 처리 (isActive = false, isDeleted = true)
        room.delete();
    }

    private Room findRoomAndCheckOwnership(Long hostId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomHubException(RoomErrorCode.ROOM_NOT_FOUND));

        if (!room.getHostId().equals(hostId)) {
            throw new RoomHubException(RoomErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (room.isDeleted()) {
            throw new RoomHubException(RoomErrorCode.ROOM_NOT_FOUND);
        }

        return room;
    }

    @Transactional(readOnly = true)
    public Room getRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomHubException(RoomErrorCode.ROOM_NOT_FOUND));

        if (room.isDeleted()) {
            throw new RoomHubException(RoomErrorCode.ROOM_NOT_FOUND);
        }

        return room;
    }

    @Transactional(readOnly = true)
    public List<Room> getRoomsByHost(Long hostId) {
        return roomRepository.findByHostId(hostId).stream()
                .filter(room -> !room.isDeleted())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Room> getAllActiveRooms() {
        return roomRepository.findAllByIsActiveTrueAndIsDeletedFalse();
    }
}
