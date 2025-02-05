package com.kindredgroup.kps.pricingentity.feeddomain.service;

import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;

public interface PriceChangedCollectionService {
    void savePriceChangedCollection(PriceChangedCollection payload);
}
