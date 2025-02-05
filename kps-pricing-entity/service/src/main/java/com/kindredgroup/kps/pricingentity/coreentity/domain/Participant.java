package com.kindredgroup.kps.pricingentity.coreentity.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;

@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Participant(
        String name,
        OffsetDateTime dateOfBirth,
        String contestType,
        String key,
        CoreEntityGender gender,
        String homeAwayState) {

    @Builder
    public Participant {

    }
}
