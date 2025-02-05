package com.kindredgroup.kps.pricingentity.persistence.coreentity.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter.EnumConverter;
import jakarta.persistence.Converter;

@Converter
public class CoreEntityGenderConverter extends EnumConverter<CoreEntityGender> {

    @Override
    protected List<CoreEntityGender> getValues() {
        return Arrays.stream(CoreEntityGender.values()).toList();
    }


}
