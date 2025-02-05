package com.kindredgroup.kps.pricingentity.feeddomain.service;

import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionChanged;

public interface OptionService {

    void save(OptionChanged payload);
}
