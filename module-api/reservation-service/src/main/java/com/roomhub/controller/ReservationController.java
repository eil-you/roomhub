package com.roomhub.controller;

import com.roomhub.model.ReservationRequest;
import com.roomhub.service.ReservationService;
import com.roomhub.entity.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    
    // 예약 생성
    @PostMapping
    public ResponseEntity<String> createReservation(@RequestBody ReservationRequest reservationRequest) {
        reservationService.createReservation(reservationRequest);
        return ResponseEntity.ok("Reservation created successfully");
    }

    // 사용자별 전체 예약 리스트 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

    // 예약 취소
    @PutMapping("/{reservationId}/cancel")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok("Reservation cancelled successfully");
    }
}
