package com.kindredgroup.kps.pricingentity.persistence.coreentity.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter.EnumConverter;
import jakarta.persistence.Converter;

@Converter
public class CompetitionTypeConverter extends EnumConverter<CompetitionType> {

    @Override
    protected List<CompetitionType> getValues() {
        return Arrays.stream(CompetitionType.values()).toList();
    }


}
