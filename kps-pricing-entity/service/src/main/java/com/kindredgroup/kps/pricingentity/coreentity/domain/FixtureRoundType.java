package com.kindredgroup.kps.pricingentity.coreentity.domain;

import java.text.MessageFormat;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kindredgroup.kps.internal.api.EnumValueSupplier;

public enum FixtureRoundType implements EnumValueSupplier {
    UNKNOWN("Unknown"),
    PRELIMINARY("Preliminary"),
    PRELIMINARY_FIRST_ROUND("PreliminaryFirstRound"),
    PRELIMINARY_SECOND_ROUND("PreliminarySecondRound"),
    PRELIMINARY_THIRD_ROUND("PreliminaryThirdRound"),
    PRELIMINARY_SEMI("PreliminarySemi"),
    PRELIMINARY_FINAL("PreliminaryFinal"),
    GROUP("Group"),
    KNOCK_OUT("KnockOut"),
    ROUND_OF_128("RoundOf128"),
    ROUND_OF_64("RoundOf64"),
    ROUND_OF_32("RoundOf32"),
    ROUND_OF_16("RoundOf16"),
    QUARTER("Quarter"),
    SEMI("Semi"),
    FINAL("Final");

    private final String value;

    FixtureRoundType(String value) {
        this.value = value;
    }

    public static FixtureRoundType of(String value) {
        return Arrays.stream(FixtureRoundType.values()).filter(status -> status.getValue().equals(value)).findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                             MessageFormat.format("Value {0} is not supported by the FixtureRoundType", value)));
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }
}
