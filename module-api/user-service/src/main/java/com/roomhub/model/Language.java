package com.roomhub.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    KOREAN("Korean"),
    ENGLISH("English"),
    JAPANESE("Japanese"),
    CHINESE("Chinese"),
    FRENCH("French"),
    GERMAN("German"),
    SPANISH("Spanish");

    private final String description;
}
