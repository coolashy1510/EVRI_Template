package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import jakarta.persistence.Converter;

@Converter
public class ContestStatusConverter extends EnumConverter<ContestStatus> {

    @Override
    protected List<ContestStatus> getValues() {
        return Arrays.stream(ContestStatus.values()).toList();
    }


}
