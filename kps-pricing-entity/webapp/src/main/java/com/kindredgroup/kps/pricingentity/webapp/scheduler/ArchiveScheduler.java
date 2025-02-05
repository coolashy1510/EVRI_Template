package com.kindredgroup.kps.pricingentity.webapp.scheduler;

import com.kindredgroup.commons.logging.logger.KpsLogger;
import com.kindredgroup.commons.logging.logger.KpsLoggerFactory;
import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ArchiveContestRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.service.ArchiveService;
import io.micrometer.core.annotation.Timed;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.kindredgroup.kps.pricingentity.webapp.logging.SchedulingLoggingAction.ARCHIVE_CONTEST_DELETE_FAILED;
import static com.kindredgroup.kps.pricingentity.webapp.logging.SchedulingLoggingAction.CONTEST_ARCHIVE_FAILED;

@Service
@Slf4j
public class ArchiveScheduler {

    private final ArchiveService archiveService;
    private final ContestRepository contestRepository;
    private final ArchiveContestRepository archiveContestRepository;
    private final KpsLogger kpsLogger = KpsLoggerFactory.getLogger(log);
    private final long archiveBulkLimit;
    private final long deleteBulkLimit;

    public ArchiveScheduler(ArchiveService archiveService, ContestRepository contestRepository,
                            ArchiveContestRepository archiveContestRepository,
                            @Value("${scheduled.archive.bulk-limit}") Long archiveBulkLimit,
                            @Value("${scheduled.deleteArchive.bulk-limit}") Long deleteBulkLimit) {
        this.archiveService = archiveService;
        this.contestRepository = contestRepository;
        this.archiveContestRepository = archiveContestRepository;
        this.archiveBulkLimit = archiveBulkLimit;
        this.deleteBulkLimit = deleteBulkLimit;
    }

    @Scheduled(fixedDelayString = "${scheduled.archive.outdatedData}", timeUnit = TimeUnit.MINUTES)
    @SchedulerLock(name = "archiveOutdatedData", lockAtMostFor = "${scheduled.archive.lock-at-most-for}",
                   lockAtLeastFor = "${scheduled.archive.lock-at-least-for}")
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveScheduler.archiveOutdatedData"}, histogram = true)
    @WithSpan
    public void archiveOutdatedData() {
        final List<Long> expired = contestRepository.getExpired(archiveBulkLimit);
        kpsLogger.info("Archiving " + expired.size() + " contests");
        final List<Long> failed = new ArrayList<>();
        expired.forEach(contestId -> {
            try {
                archiveService.archive(contestId);
            } catch (Exception e) {
                kpsLogger.error(CONTEST_ARCHIVE_FAILED, e::getMessage, Map.of(), e);
                failed.add(contestId);
            }
        });
        kpsLogger.info("Archiving finished." + (failed.isEmpty() ? "" : " Failed contests: " + failed));
    }

    @Scheduled(fixedDelayString = "${scheduled.deleteArchive.frequency}", timeUnit = TimeUnit.MINUTES)
    @SchedulerLock(name = "deleteArchivedData", lockAtMostFor = "${scheduled.archive.lock-at-most-for}",
                   lockAtLeastFor = "${scheduled.archive.lock-at-least-for}")
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "ArchiveScheduler.deleteArchivedData"}, histogram = true)
    @WithSpan
    public void deleteArchivedData() {
        List<Long> expired = archiveContestRepository.getExpired(deleteBulkLimit);
        kpsLogger.info("Deleting " + expired.size() + " archived contests");
        List<Long> failed = new ArrayList<>();
        expired.forEach(contestId -> {
            try {
                archiveService.delete(contestId);
            } catch (Exception e) {
                kpsLogger.error(ARCHIVE_CONTEST_DELETE_FAILED, e::getMessage, Map.of(), e);
                failed.add(contestId);
            }
        });
        kpsLogger.info("Deleting Archived Records finished." + (failed.isEmpty() ? "" : " Failed Contests: " + failed));
    }

}
