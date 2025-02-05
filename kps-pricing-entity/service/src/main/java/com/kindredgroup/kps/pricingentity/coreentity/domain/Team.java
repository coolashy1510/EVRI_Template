package com.kindredgroup.kps.pricingentity.coreentity.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Team(
        String name,
        List<Participant> participants,
        String contestType,
        CoreEntityGender gender,
        String key) {

    @Builder
    public Team {

    }
}
