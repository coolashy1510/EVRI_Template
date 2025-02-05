package com.kindredgroup.kps.pricingentity.persistence.coreentity.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionAgeCategory;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter.EnumConverter;
import jakarta.persistence.Converter;

@Converter
public class CompetitionAgeCategoryConverter extends EnumConverter<CompetitionAgeCategory> {

    @Override
    protected List<CompetitionAgeCategory> getValues() {
        return Arrays.stream(CompetitionAgeCategory.values()).toList();
    }


}
