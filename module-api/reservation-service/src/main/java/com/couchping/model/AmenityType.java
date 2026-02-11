package com.couchping.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AmenityType {
    WIFI("free-wifi"),
    KITCHEN("kitchen"),
    WASHING_MACHINE("washing-machine"),
    AIR_CONDITIONING("air-conditioning"),
    HEATING("heating"),
    TOWEL("towel"),
    SHAMPOO("shampoo"),
    HAIR_DRYER("hair-dryer"),
    PARKING("parking"),
    PETS_ALLOWED("pets-allowed"),
    SMOKING_ALLOWED("smoking-allowed");

    private final String description;
}
