package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvided;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Builder
@JsonDeserialize(builder = OutcomeResult.OutcomeResultBuilder.class)
public class OutcomeResult implements FeedProvided {
    private final String contestKey;
    private boolean isManuallyControlled;
    private final List<OutcomeFraction> outcomeFractions;
    private final String propositionKey;
    private String source;
    private FeedProvider provider;
    private OffsetDateTime timeStampUtc;

    public OutcomeResult(String contestKey, boolean isManuallyControlled, List<OutcomeFraction> outcomeFractions, String propositionKey, String source, FeedProvider provider, OffsetDateTime timeStampUtc) {
        this.contestKey = contestKey;
        this.isManuallyControlled = isManuallyControlled;
        this.outcomeFractions = outcomeFractions;
        this.propositionKey = propositionKey;
        this.source = source;
        this.provider = provider;
        this.timeStampUtc = timeStampUtc;
    }

    public OutcomeResult(String contestKey, boolean isManuallyControlled, String propositionKey, String source, FeedProvider provider, OffsetDateTime timeStampUtc) {
        this.contestKey = contestKey;
        this.isManuallyControlled = isManuallyControlled;
        this.outcomeFractions =  new ArrayList<>();
        this.propositionKey = propositionKey;
        this.source = source;
        this.provider = provider;
        this.timeStampUtc = timeStampUtc;
    }

    public OutcomeResult(String contestKey, String propositionKey) {
        this.contestKey = contestKey;
        this.propositionKey = propositionKey;
        this.outcomeFractions = new ArrayList<>();
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OutcomeResultBuilder {

    }

    @Override
    public FeedProvider provider() {
        return provider;
    }
}
