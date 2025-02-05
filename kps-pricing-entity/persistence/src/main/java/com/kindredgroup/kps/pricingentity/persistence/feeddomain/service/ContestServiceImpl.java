package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import java.util.Optional;

import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.feeddomain.service.ContestService;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.mapper.FeedDomainMapper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ContestServiceImpl implements ContestService {
    private final ContestRepository contestRepository;

    public ContestServiceImpl(ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "getContestByKey"},
           histogram = true)
    public Optional<com.kindredgroup.kps.internal.api.pricingdomain.Contest> getByKey(String contestKey) {
        return findByKey(contestKey).map(FeedDomainMapper::getContest);
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "findContestByKey"},
           histogram = true)
    Optional<Contest> findByKey(String contestKey) {
        return contestRepository.findByKey(contestKey);
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "saveContest"},
           histogram = true)
    public void save(com.kindredgroup.kps.internal.api.pricingdomain.Contest contest) {
        final String contestKey = contest.contestKey();
        final Optional<Contest> persistedCandidate = contestRepository.findByKey(contestKey);
        Contest persisted;
        if (persistedCandidate.isPresent()) {
            persisted = persistedCandidate.get();
        } else {
            persisted = new Contest();
            persisted.setKey(contest.contestKey());
            persisted.setName(contest.name());
            persisted.setType(ContestType.of(contest.contestType()));
            //TODO:n.shvinagir:2023-02-21: pull and store core entities from Entity Manager
        }
        persisted.setStatus(contest.status());
        persisted.setStartDateTime(contest.startDateTimeUtc());
        contestRepository.save(persisted);
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "getContestType"},
           histogram = true)
    public Optional<ContestType> getContestType(String contestKey) {
        return contestRepository.getContestTypeByKey(contestKey);
    }

}
