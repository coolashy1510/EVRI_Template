package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ArchiveRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ArchiveService {
    private final ArchiveRepository archiveRepository;

    public ArchiveService(ArchiveRepository archiveRepository) {
        this.archiveRepository = archiveRepository;
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveService.archive"},
           histogram = true)
    public void archive(Long contestId) {
        archiveRepository.archiveContest(contestId);
        archiveRepository.archivePropositions(contestId);
        archiveRepository.archiveOptions(contestId);
        archiveRepository.archiveVariants(contestId);
        archiveRepository.archivePlaceholders(contestId);
        archiveRepository.archivePrices(contestId);
        archiveRepository.archiveOutcomes(contestId);
        archiveRepository.deletePrices(contestId);
        archiveRepository.deleteOutcomes(contestId);
        archiveRepository.deleteOptions(contestId);
        archiveRepository.deleteVariants(contestId);
        archiveRepository.deletePlaceholders(contestId);
        archiveRepository.deletePropositions(contestId);
        archiveRepository.deleteContest(contestId);
    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveService.delete"},
           histogram = true)
    public void delete(Long contestId) {
        archiveRepository.deleteArchivedPrices(contestId);
        archiveRepository.deleteArchivedOutcomes(contestId);
        archiveRepository.deleteArchivedOptions(contestId);
        archiveRepository.deleteArchivedVariants(contestId);
        archiveRepository.deleteArchivedPlaceholders(contestId);
        archiveRepository.deleteArchivedPropositions(contestId);
        archiveRepository.deleteArchivedContest(contestId);
    }

}
