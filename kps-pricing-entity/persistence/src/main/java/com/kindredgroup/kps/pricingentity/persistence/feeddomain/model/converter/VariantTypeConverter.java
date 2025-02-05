package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.internal.api.VariantType;
import jakarta.persistence.Converter;

@Converter
public class VariantTypeConverter extends EnumConverter<VariantType> {
    @Override
    protected List<VariantType> getValues() {
        return Arrays.stream(VariantType.values()).toList();
    }
}
