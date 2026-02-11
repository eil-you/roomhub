package com.couchping.service;

import com.couchping.entity.Room;
import com.couchping.exception.CouchPingException;
import com.couchping.model.RoomErrorCode;
import com.couchping.model.RoomRequest;
import com.couchping.repository.RoomRepository;
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
     * 1. 숙박 시설 등록
     */
    @Transactional
    public Long registerRoom(Long hostId, RoomRequest request) {
        Room room = request.toEntity(hostId);
        return roomRepository.save(room).getId();
    }

    /**
     * 2. 숙박 시설 정보 수정
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
     * 3. 숙박 시설 활성화 상태 변경 (ON/OFF)
     */
    @Transactional
    public void toggleActiveStatus(Long hostId, Long roomId, boolean isActive) {
        Room room = findRoomAndCheckOwnership(hostId, roomId);
        room.updateStatus(isActive);
    }

    /**
     * 4. 확정된 예약 건수 조회
     */
    @Transactional(readOnly = true)
    public int getConfirmedReservationCount(Long roomId) {
        // ???고뒎 ??類λ룴???고뱺???筌먦끉??????고뒎 嶺뚮ㅄ維뽨빳???띠럾??筌? ?띠룇裕?遺븍ご??꾩룇瑗??(ReservationService.cancelAllByRoomId 嶺뚣볝늾??
        // ???遊붋?釉뚯뫒? ???쳜?猿낆뿉??댁몠????????????롪퍔???????굵 ?熬곣뫗??뼨??熬곥굥????????????곕????덈펲.
        return (int) reservationService.findAllByRoomId(roomId).stream()
                .filter(r -> r.getStatus().name().equals("CONFIRMED"))
                .count();
    }

    /**
     * 5. ???덇틬 ????(Soft Delete)
     * ?筌먦끉??????고뒎?????덈펲嶺?嶺뚮ㅄ維筌????쳛??嶺뚳퐣瑗????겶????逾???곌랜?繹???덈펲.
     */
    @Transactional
    public void deleteRoom(Long hostId, Long roomId) {
        Room room = findRoomAndCheckOwnership(hostId, roomId);

        // 1. ????????덇틬??嶺뚮ㅄ維獄??筌먦끉??????고뒎 ???쳛??(ReservationService???熬곣뫗肉?
        reservationService.cancelAllByRoomId(roomId);

        // 2. 숙박 시설 삭제 처리 (isActive = false, isDeleted = true)
        room.delete();
    }

    private Room findRoomAndCheckOwnership(Long hostId, Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CouchPingException(RoomErrorCode.ROOM_NOT_FOUND));

        if (!room.getHostId().equals(hostId)) {
            throw new CouchPingException(RoomErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (room.isDeleted()) {
            throw new CouchPingException(RoomErrorCode.ROOM_NOT_FOUND);
        }

        return room;
    }

    @Transactional(readOnly = true)
    public Room getRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CouchPingException(RoomErrorCode.ROOM_NOT_FOUND));

        if (room.isDeleted()) {
            throw new CouchPingException(RoomErrorCode.ROOM_NOT_FOUND);
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

