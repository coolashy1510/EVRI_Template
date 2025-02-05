package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvided;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import lombok.Builder;


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OptionChanged(String contestKey,
                            String propositionKey,
                            String optionKey,
                            FeedProvider provider,
                            boolean bettingOpen,
                            Set<OptionManuallyControlledField> manuallyControlledFields,
                            String name) implements FeedProvided {
    @Builder
    public OptionChanged {
    }
}
