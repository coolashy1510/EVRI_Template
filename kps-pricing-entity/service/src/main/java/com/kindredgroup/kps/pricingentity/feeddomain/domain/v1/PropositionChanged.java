package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvided;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PropositionChanged(String contestKey,
                                 String propositionKey,
                                 FeedProvider provider,
                                 boolean bettingOpen,
                                 boolean cancelled,
                                 boolean cashOutOpen,
                                 Set<PropositionManuallyControlledField> manuallyControlledFields,
                                 String name) implements FeedProvided {
    @Builder
    public PropositionChanged {
    }
}
