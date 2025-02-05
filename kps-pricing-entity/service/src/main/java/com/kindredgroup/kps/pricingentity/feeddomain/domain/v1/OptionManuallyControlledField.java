package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OptionManuallyControlledField {
    BETTING_OPEN("BettingOpen"),
    NAME("Name");

    @JsonValue
    public final String value;
}
