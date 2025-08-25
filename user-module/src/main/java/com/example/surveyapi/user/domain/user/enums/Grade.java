package com.example.surveyapi.user.domain.user.enums;



public enum Grade {
    MASTER(null),
    DIAMOND(MASTER),
    PLATINUM(DIAMOND),
    GOLD(PLATINUM),
    SILVER(GOLD),
    BRONZE(SILVER);

    private final Grade next;

    Grade (Grade next) {
        this.next = next;
    }

    public Grade next() {
        return next;
    }
}
