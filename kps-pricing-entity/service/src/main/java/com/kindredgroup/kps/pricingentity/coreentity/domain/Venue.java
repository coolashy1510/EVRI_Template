package com.kindredgroup.kps.pricingentity.coreentity.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Venue(String name, String contestType, String countryCode, String key) {
    @Builder
    public Venue {

    }
}
