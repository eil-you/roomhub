package com.couchping.entity;

import com.couchping.model.AmenityType;
import com.couchping.model.PreferredGender;
import com.couchping.model.RoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long hostId; // ?몄뒪???좎? ID

    @Column(nullable = false)
    private String title; // ?숈냼 ?쒕ぉ

    @Column(columnDefinition = "TEXT")
    private String description; // ?숈냼 ?곸꽭 ?ㅻ챸

    @Column
    private String imageUrl; // ?숈냼 ????ъ쭊 寃쎈줈

    @Column(nullable = false)
    private String location; // ??듭쟻???꾩튂

    @Column(nullable = false)
    private Integer price; // 1諛뺣떦 媛寃?

    @Column(nullable = false)
    private Integer capacity; // 理쒕? ?섏슜 ?몄썝

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType; // COUCH, PRIVATE_ROOM, SHARED_ROOM

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PreferredGender preferredGender; // ANY, MALE, FEMALE

    @ElementCollection(targetClass = AmenityType.class)
    @CollectionTable(name = "room_amenity", joinColumns = @JoinColumn(name = "room_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "amenity_type")
    private List<AmenityType> amenities = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String initialQuestion; // ?몄뒪?멸? 寃뚯뒪?몄뿉寃?臾삳뒗 泥?吏덈Ц

    @Column(nullable = false)
    private boolean isActive = true; // ?숈냼 ?쒖꽦???곹깭

    @Column(nullable = false)
    private boolean isDeleted = false; // ??젣 ?щ? (Soft Delete)

    @Column(nullable = false)
    private Double hostRating = 0.0; // ?몄뒪?몄쓽 理쒖떊 ?좊ː ?됱젏 (罹먯떛??

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomImage> images = new ArrayList<>();

    @Builder
    public Room(Long hostId, String title, String description, String imageUrl, String location,
            Integer price, Integer capacity, String initialQuestion, RoomType roomType,
            PreferredGender preferredGender) {
        this.hostId = hostId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.location = location;
        this.price = price;
        this.capacity = capacity;
        this.initialQuestion = initialQuestion;
        this.isActive = true;
        this.isDeleted = false;
        this.roomType = roomType;
        this.preferredGender = preferredGender;
    }

    // ?몄쓽 硫붿꽌?? ?대?吏 異붽?
    public void addImage(String imageUrl, int sortOrder) {
        RoomImage roomImage = RoomImage.builder()
                .room(this)
                .imageUrl(imageUrl)
                .sortOrder(sortOrder)
                .build();
        this.images.add(roomImage);
    }

    // ?몄쓽 硫붿꽌?? ?몄쓽?쒖꽕 異붽?
    public void addAmenity(AmenityType amenityType) {
        if (!this.amenities.contains(amenityType)) {
            this.amenities.add(amenityType);
        }
    }

    public void updateStatus(boolean isActive) {
        this.isActive = isActive;
    }

    public void delete() {
        this.isDeleted = true;
        this.isActive = false;
    }

    public void updateHostRating(Double hostRating) {
        this.hostRating = hostRating;
    }

    public void update(String title, String description, String imageUrl, String location,
            Integer price, Integer capacity, String initialQuestion,
            RoomType roomType, PreferredGender preferredGender,
            List<AmenityType> amenities) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.location = location;
        this.price = price;
        this.capacity = capacity;
        this.initialQuestion = initialQuestion;
        this.roomType = roomType;
        this.preferredGender = preferredGender;
        this.amenities.clear();
        if (amenities != null) {
            this.amenities.addAll(amenities);
        }
    }
}
