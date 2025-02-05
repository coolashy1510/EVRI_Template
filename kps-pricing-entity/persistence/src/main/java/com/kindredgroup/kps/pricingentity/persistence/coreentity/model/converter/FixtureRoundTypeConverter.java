package com.kindredgroup.kps.pricingentity.persistence.coreentity.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.pricingentity.coreentity.domain.FixtureRoundType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter.EnumConverter;
import jakarta.persistence.Converter;

@Converter
public class FixtureRoundTypeConverter extends EnumConverter<FixtureRoundType> {

    @Override
    protected List<FixtureRoundType> getValues() {
        return Arrays.stream(FixtureRoundType.values()).toList();
    }


}
