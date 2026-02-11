package com.couchping.entity;

import com.couchping.model.ReservationStatus;
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
    private long userId; // ?덉빟??(User-service??User ID)

    @Column(nullable = false)
    private long roomId; // ?덉빟??諛?(Room-service??Room ID)

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status; // ?덉빟 ?곹깭(PENDING, CONFIRMED, CANCELLED)

    @Builder
    public Reservation(long userId, long roomId, LocalDate checkInDate, LocalDate checkOutDate, int totalPrice) {
        this.userId = userId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = ReservationStatus.PENDING; // 湲곕낯媛?: 寃곗젣 ?湲?
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }
}