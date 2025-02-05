package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PropositionManuallyControlledField {
    BETTING_OPEN("BettingOpen"),
    CANCELLED("Cancelled"),
    CASH_OUT_OPEN("CashOutOpen"),
    NAME("Name");

    @JsonValue
    public final String value;
}
