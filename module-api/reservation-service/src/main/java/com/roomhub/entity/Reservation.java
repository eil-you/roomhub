package com.roomhub.entity;

import com.roomhub.model.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Reservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long userId; // 예약자 (User-service의 User ID)

    @Column(nullable = false)
    private long roomId; // 예약한 방 (Room-service의 Room ID)

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status; // 예약 상태(PENDING, CONFIRMED, CANCELLED)

    @Builder
    public Reservation(long userId, long roomId, LocalDate checkInDate, LocalDate checkOutDate, int totalPrice) {
        this.userId = userId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = ReservationStatus.PENDING; // 기본값 : 결제 대기
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }
}