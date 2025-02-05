package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.internal.api.pricingdomain.QuantContractTypeClassName;
import jakarta.persistence.Converter;

@Converter
public class QuantContractTypeConverter extends EnumConverter<QuantContractTypeClassName> {
    @Override
    protected List<QuantContractTypeClassName> getValues() {
        return Arrays.stream(QuantContractTypeClassName.values()).toList();
    }
}
