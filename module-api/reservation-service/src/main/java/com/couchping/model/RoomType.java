package com.couchping.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
    COUCH("Couch"),
    PRIVATE_ROOM("Private Room"),
    SHARED_ROOM("Shared Room");

    private final String description;
}
