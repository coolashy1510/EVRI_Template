package com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.converter;

import java.util.Arrays;
import java.util.List;

import com.kindredgroup.kps.internal.api.pricingdomain.QuantMarketTypeClassName;
import jakarta.persistence.Converter;

@Converter
public class QuantMarketTypeConverter extends EnumConverter<QuantMarketTypeClassName> {
    @Override
    protected List<QuantMarketTypeClassName> getValues() {
        return Arrays.stream(QuantMarketTypeClassName.values()).toList();
    }
}
