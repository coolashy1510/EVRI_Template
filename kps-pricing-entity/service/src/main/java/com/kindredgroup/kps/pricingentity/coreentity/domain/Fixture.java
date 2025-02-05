package com.kindredgroup.kps.pricingentity.coreentity.domain;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Fixture(String name,
                      String contestType,
                      OffsetDateTime startDateTimeUtc,
                      Venue venue,
                      String groupName,
                      String roundType,
                      List<Team> teams,
                      List<Participant> participants,
                      Tournament tournament,
                      Competition competition,
                      String key) {

    @Builder
    public Fixture {

    }
}
