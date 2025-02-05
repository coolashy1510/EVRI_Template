package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import java.text.MessageFormat;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonValue;
import com.kindredgroup.kps.internal.api.EnumValueSupplier;

// TODO:nikita.shvinagir:2023-01-30: consider using shared domain entities in all packages: persistence, messaging, etc.
public enum OutcomeType implements EnumValueSupplier {
    LINE_OUTCOME("LineOutcome"),
    MARGIN_OUTCOME("MarginOutcome"),
    OVER_UNDER_OUTCOME("OverUnderOutcome"),
    PLAIN_OUTCOME("PlainOutcome"),
    TOTE_OUTCOME("ToteOutcome");

    private final String value;

    OutcomeType(String value) {
        this.value = value;
    }

    public static OutcomeType of(String value) {
        return Arrays.stream(OutcomeType.values()).filter(type -> type.getValue().equals(value)).findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                             MessageFormat.format("Value {0} is not suppoerted by the VariantType", value)));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
