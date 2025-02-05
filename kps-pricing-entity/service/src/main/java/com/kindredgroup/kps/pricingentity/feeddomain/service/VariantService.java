package com.kindredgroup.kps.pricingentity.feeddomain.service;

import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantChanged;

public interface VariantService {
    void save(VariantChanged payload);
}
