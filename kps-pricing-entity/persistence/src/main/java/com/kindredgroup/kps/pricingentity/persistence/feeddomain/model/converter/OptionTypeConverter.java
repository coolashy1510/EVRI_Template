package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.internal.api.OptionType;
import jakarta.persistence.Converter;

@Converter
public class OptionTypeConverter extends EnumConverter<OptionType> {
    @Override
    protected List<OptionType> getValues() {
        return Arrays.stream(OptionType.values()).toList();
    }
}
