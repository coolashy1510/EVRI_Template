package com.kindredgroup.kps.pricingentity.coreentity.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Competition(String name,
                          String contestType,
                          String ageCategory,
                          CoreEntityGender gender,
                          String competitionType,
                          List<Tournament> tournaments,
                          String key) {
    @Builder
    public Competition {

    }
}
