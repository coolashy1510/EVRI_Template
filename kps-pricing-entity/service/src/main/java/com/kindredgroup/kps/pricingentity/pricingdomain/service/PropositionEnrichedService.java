package com.kindredgroup.kps.pricingentity.pricingdomain.service;

import com.kindredgroup.kps.internal.api.pricingdomain.PropositionEnriched;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2;

public interface PropositionEnrichedService {
    PropositionEnriched enriched(Proposition proposition);

    PropositionEnriched enriched(PropositionV2 value);
}
