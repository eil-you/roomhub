package com.couchping.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Interest {
    BOARD_GAME("Board Games"),
    READING("Reading"),
    HIKING("Hiking"),
    COOKING("Cooking"),
    TRAVELING("Traveling"),
    PHOTOGRAPHY("Photography"),
    MUSIC("Music"),
    MOVIE("Movie"),
    SPORTS("Sports");

    private final String description;
}
