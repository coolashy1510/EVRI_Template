package com.kindredgroup.kps.pricingentity.coreentity.domain;

import java.text.MessageFormat;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kindredgroup.kps.internal.api.EnumValueSupplier;

public enum CoreEntityGender implements EnumValueSupplier {
    NOT_KNOWN("NotKnown"),
    MALE("Male"),
    FEMALE("Female"),
    MIXED("Mixed");

    private final String value;

    CoreEntityGender(String value) {
        this.value = value;
    }

    public static CoreEntityGender of(String value) {
        return Arrays.stream(CoreEntityGender.values()).filter(status -> status.getValue().equals(value)).findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                             MessageFormat.format("Value {0} is not supported by the CompetitionGender", value)));
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }
}
