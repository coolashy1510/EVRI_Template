package com.kindredgroup.kps.pricingentity.pricingdomain.service;

import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollectionEnriched;

public interface PriceChangedCollectionEnrichedService {
    PriceChangedCollectionEnriched enriched(PriceChangedCollection priceChangedCollection);
}
