package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import jakarta.persistence.Converter;

@Converter
public class ContestTypeConverter extends EnumConverter<ContestType> {

    @Override
    protected List<ContestType> getValues() {
        return Arrays.stream(ContestType.values()).toList();
    }
}
