package com.roomhub.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_image")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private int sortOrder; // 이미지 출력 순서

    @Builder
    public RoomImage(Room room, String imageUrl, int sortOrder) {
        this.room = room;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }
}
