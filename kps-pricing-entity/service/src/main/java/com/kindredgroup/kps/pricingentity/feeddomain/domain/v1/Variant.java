package com.kindredgroup.kps.pricingentity.feeddomain.domain.v1;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.kindredgroup.kps.internal.api.VariantType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@ToString
@Builder
@JsonDeserialize(builder = Variant.VariantBuilder.class)
public class Variant {
    private String variantKey;
    private VariantType variantType;
    private boolean bettingOpen;
    private String name;
    private Set<VariantManuallyControlledField> manuallyControlledFields;

    @JsonPOJOBuilder(withPrefix = "")
    public static class VariantBuilder {

    }
}
