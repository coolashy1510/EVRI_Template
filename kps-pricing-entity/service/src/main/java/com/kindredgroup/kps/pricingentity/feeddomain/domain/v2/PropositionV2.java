package com.kindredgroup.kps.pricingentity.feeddomain.domain.v2;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kindredgroup.kps.internal.api.pricingdomain.Argument;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvided;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionManuallyControlledField;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Variant;
import lombok.Builder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record PropositionV2(String contestKey,
                            String propositionKey,
                            boolean cashOutOpen,
                            String name,
                            List<OptionV2> options,
                            String propositionType,
                            FeedProvider provider,
                            List<Variant> variants,
                            List<Argument> arguments,
                            Set<PropositionManuallyControlledField> manuallyControlledFields) implements FeedProvided {
    @Builder
    public PropositionV2 {
    }

    @Override
    public List<Argument> arguments() {
        return arguments == null ? List.of() : arguments;
    }
}
