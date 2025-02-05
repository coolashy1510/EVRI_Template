package com.kindredgroup.kps.pricingentity.feeddomain.service;

import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;

import java.util.List;

public interface OutcomeResultService {
    void save(OutcomeResult payload);
    List<OutcomeResult> getOutcomeResult(String contestKey);
}
