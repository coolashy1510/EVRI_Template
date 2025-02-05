package com.kindredgroup.kps.pricingentity.feeddomain.service;

import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2;

public interface PropositionService {
    void save(Proposition payload);

    void update(PropositionChanged payload);

    void save(PropositionV2 payload);
}
