package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeType;
import jakarta.persistence.Converter;

@Converter
public class OutcomeTypeConverter extends EnumConverter<OutcomeType> {
    @Override
    protected List<OutcomeType> getValues() {
        return Arrays.stream(OutcomeType.values()).toList();
    }
}
