package com.kindredgroup.kps.pricingentity.feeddomain.service;

import java.util.Optional;

import com.kindredgroup.kps.internal.api.pricingdomain.Contest;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;

public interface ContestService {
    Optional<Contest> getByKey(String contestKey);
    void save(Contest contest);
    Optional<ContestType> getContestType(String key);
}
