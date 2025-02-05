package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kindredgroup.kps.internal.api.pricingdomain.QuantContractType;
import com.kindredgroup.kps.internal.api.pricingdomain.QuantMarketType;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OutcomeFraction(String optionKey,
                              QuantMarketType quantMarketType,
                              String variantKey,
                              QuantContractType quantContractType,
                              Fraction refundFraction,
                              Fraction winFraction) {
    @Builder
    public OutcomeFraction {
    }
}
