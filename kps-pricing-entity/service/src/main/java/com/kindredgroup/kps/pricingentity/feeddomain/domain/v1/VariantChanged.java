package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvided;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VariantChanged(String contestKey,
                             String propositionKey,
                             String variantKey,
                             FeedProvider provider,
                             boolean bettingOpen,
                             Set<VariantManuallyControlledField> manuallyControlledFields,
                             String name) implements FeedProvided {
    @Builder
    public VariantChanged {
    }
}
