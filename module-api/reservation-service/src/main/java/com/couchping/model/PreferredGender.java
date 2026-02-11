package com.couchping.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PreferredGender {
    ANY("Any"),
    MALE("Male"),
    FEMALE("Female");

    private final String description;
}
