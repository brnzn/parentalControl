package com.parentalcontrol;

import com.parentalcontrol.exception.UnknownAgeRatingException;

public enum ParentalControlLevel {
    U("U"),
    PG("PG"),
    TWELVE("12"),
    FIFTEEN("15"),
    EIGHTEEN("18");

    private final String value;

    ParentalControlLevel(String value) {
        this.value = value;
    }

    public boolean isSuitableFor(ParentalControlLevel level) {
        return this.compareTo(level) >= 0;
    }

    public static ParentalControlLevel toParentalControlLevel(String value) {
        for(ParentalControlLevel level: values()) {
            if (level.value.equals(value)) {
                return level;
            }
        }

        throw new UnknownAgeRatingException(String.format("Unknown Age Rating [%s]", value));
    }
}
