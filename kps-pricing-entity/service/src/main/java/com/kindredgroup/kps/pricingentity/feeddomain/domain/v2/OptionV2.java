package com.kindredgroup.kps.pricingentity.feeddomain.domain.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.pricingdomain.Argument;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionManuallyControlledField;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@ToString
@Getter
@Builder
@JsonDeserialize(builder = OptionV2.OptionBuilder.class)
public class OptionV2 {
    private String optionKey;
    private OptionType optionType;
    private boolean bettingOpen;
    private String name;
    private Set<OptionManuallyControlledField> manuallyControlledFields;
    private List<Argument> arguments;

    public List<Argument> getArguments() {
        return arguments == null ? new ArrayList<>() : arguments;
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OptionBuilder {

    }
}
