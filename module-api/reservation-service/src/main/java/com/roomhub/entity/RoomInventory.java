package com.roomhub.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "room_inventory", uniqueConstraints = {
        // 특정 날짜에 특정 방은 하나만 존재
        @UniqueConstraint(columnNames = { "roomId", "date" })
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomInventory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long roomId;

    @Column(nullable = false)
    private LocalDate date;

    // 남은 객실 수
    @Column(nullable = false, columnDefinition = "int default 0")
    private int stock;

    @Builder
    public RoomInventory(long roomId, LocalDate date, int stock) {
        this.roomId = roomId;
        this.date = date;
        this.stock = stock;
    }

    public void decreaseStock() {
        this.stock = this.stock - 1;
    }

    public void increaseStock() {
        this.stock = this.stock + 1;
    }
}
