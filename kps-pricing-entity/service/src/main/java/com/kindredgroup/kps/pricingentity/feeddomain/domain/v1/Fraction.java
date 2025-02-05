package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;


@JsonIgnoreProperties(ignoreUnknown = true)
public record Fraction(int numerator, int denominator) {
    @Builder
    public Fraction {
    }
}
