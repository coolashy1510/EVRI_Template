package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvided;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import lombok.Builder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record Proposition(String contestKey,
                          String propositionKey,
                          boolean cashOutOpen,
                          String name,
                          List<Option> options,
                          String propositionType,
                          FeedProvider provider,
                          List<Variant> variants,
                          Map<String, String> placeholders,
                          Set<PropositionManuallyControlledField> manuallyControlledFields) implements FeedProvided {
    @Builder
    public Proposition {
    }

    @Override
    public Map<String, String> placeholders() {
        return placeholders == null ? Map.of() : placeholders;
    }
}
