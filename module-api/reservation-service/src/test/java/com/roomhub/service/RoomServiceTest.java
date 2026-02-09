package com.roomhub.service;

import com.roomhub.entity.Room;
import com.roomhub.exception.RoomHubException;
import com.roomhub.model.*;
import com.roomhub.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("숙박 등록 성공")
    void registerRoom_Success() {
        // given
        Long hostId = 1L;
        RoomRequest request = createRoomRequest("평화로운 집");
        Room room = request.toEntity(hostId);

        // Mocking para o setId (opcional se não precisar do id no teste, mas boa
        // prática)
        // Como o Room não tem setter para ID, o save original do JpaRepository
        // retornaria o objeto com ID.
        given(roomRepository.save(any(Room.class))).willReturn(room);

        // when
        Long roomId = roomService.registerRoom(hostId, request);

        // then
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    @DisplayName("숙소 정보 수정 성공")
    void updateRoom_Success() {
        // given
        Long hostId = 1L;
        Long roomId = 10L;
        Room existingRoom = createRoom(hostId, "옛날 제목");
        RoomRequest updateRequest = createRoomRequest("새로운 제목");

        given(roomRepository.findById(roomId)).willReturn(Optional.of(existingRoom));

        // when
        roomService.updateRoom(hostId, roomId, updateRequest);

        // then
        assertEquals("새로운 제목", existingRoom.getTitle());
    }

    @Test
    @DisplayName("숙소 수정 실패 - 호스트가 다름")
    void updateRoom_Fail_Unauthorized() {
        // given
        Long hostId = 1L;
        Long otherHostId = 2L;
        Long roomId = 10L;
        Room existingRoom = createRoom(otherHostId, "다른 사람 방");
        RoomRequest updateRequest = createRoomRequest("수정 시도");

        given(roomRepository.findById(roomId)).willReturn(Optional.of(existingRoom));

        // when & then
        assertThrows(RoomHubException.class, () -> roomService.updateRoom(hostId, roomId, updateRequest));
    }

    @Test
    @DisplayName("숙소 온/오프 토글 성공")
    void toggleActiveStatus_Success() {
        // given
        Long hostId = 1L;
        Long roomId = 10L;
        Room room = createRoom(hostId, "테스트 방");
        assertTrue(room.isActive());

        given(roomRepository.findById(roomId)).willReturn(Optional.of(room));

        // when
        roomService.toggleActiveStatus(hostId, roomId, false);

        // then
        assertFalse(room.isActive());
    }

    @Test
    @DisplayName("숙소 삭제 성공 - 예약 취소 로직 포함")
    void deleteRoom_Success() {
        // given
        Long hostId = 1L;
        Long roomId = 10L;
        Room room = createRoom(hostId, "삭제될 방");

        given(roomRepository.findById(roomId)).willReturn(Optional.of(room));

        // when
        roomService.deleteRoom(hostId, roomId);

        // then
        assertTrue(room.isDeleted());
        assertFalse(room.isActive());
        verify(reservationService, times(1)).cancelAllByRoomId(roomId);
    }

    // Helper Methods
    private RoomRequest createRoomRequest(String title) {
        return new RoomRequest(
                title, "설명", "url", "서울", 10000, 2, "고양이 좋으세요?",
                RoomType.PRIVATE_ROOM, PreferredGender.ANY, new ArrayList<>());
    }

    private Room createRoom(Long hostId, String title) {
        return Room.builder()
                .hostId(hostId)
                .title(title)
                .description("설명")
                .imageUrl("url")
                .location("서울")
                .price(10000)
                .capacity(2)
                .initialQuestion("질문")
                .roomType(RoomType.PRIVATE_ROOM)
                .preferredGender(PreferredGender.ANY)
                .build();
    }
}
