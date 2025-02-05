package com.kindredgroup.kps.pricingentity.coreentity.domain;

import java.text.MessageFormat;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kindredgroup.kps.internal.api.EnumValueSupplier;

public enum CompetitionAgeCategory implements EnumValueSupplier {
    NOT_APPLICABLE("NotApplicable"),
    UNCONFIRMED("Unconfirmed"),
    Y10("Y10"),
    U11("U11"),
    U12("U12"),
    U13("U13"),
    U14("U14"),
    U15("U15"),
    U16("U16"),
    U17("U17"),
    U18("U18"),
    U19("U19"),
    U20("U20"),
    U21("U21"),
    U22("U22"),
    U23("U23"),
    JUNIORS("Juniors"),
    SENIOR("Senior"),
    YOUTH("Youth");

    private final String value;

    CompetitionAgeCategory(String value) {
        this.value = value;
    }

    public static CompetitionAgeCategory of(String value) {
        return Arrays.stream(CompetitionAgeCategory.values()).filter(status -> status.getValue().equals(value)).findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                             MessageFormat.format("Value {0} is not supported by the CompetitionAgeCategory", value)));
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }
}
