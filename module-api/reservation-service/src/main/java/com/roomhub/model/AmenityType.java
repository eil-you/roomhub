package com.roomhub.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AmenityType {
    WIFI("무선 인터넷"),
    KITCHEN("주방"),
    WASHING_MACHINE("세탁기"),
    AIR_CONDITIONING("에어컨"),
    HEATING("난방"),
    TOWEL("수건"),
    SHAMPOO("샴푸"),
    HAIR_DRYER("헤어드라이어"),
    PARKING("주차 공간"),
    PETS_ALLOWED("반려동물 동반 가능"),
    SMOKING_ALLOWED("흡연 가능");

    private final String description;
}
