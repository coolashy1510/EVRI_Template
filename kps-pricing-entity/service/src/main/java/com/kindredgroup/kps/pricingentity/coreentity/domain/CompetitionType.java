package com.kindredgroup.kps.pricingentity.coreentity.domain;

import java.text.MessageFormat;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kindredgroup.kps.internal.api.EnumValueSupplier;

public enum CompetitionType implements EnumValueSupplier {
    NOT_APPLICABLE("NotApplicable"),
    CUP("Cup"),
    LEAGUE("League");

    private final String value;

    CompetitionType(String value) {
        this.value = value;
    }

    public static CompetitionType of(String value) {
        return Arrays.stream(CompetitionType.values()).filter(status -> status.getValue().equals(value)).findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                             MessageFormat.format("Value {0} is not supported by the CompetitionType", value)));
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }
}
