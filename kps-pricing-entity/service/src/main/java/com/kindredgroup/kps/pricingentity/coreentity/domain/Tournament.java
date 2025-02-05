package com.kindredgroup.kps.pricingentity.coreentity.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;


@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Tournament(String name,
                         List<Fixture> fixtures,
                         Competition competition,
                         OffsetDateTime startDateTimeUtc,
                         OffsetDateTime endDateTimeUtc,
                         String key) {

    @Builder
    public Tournament {

    }
}
