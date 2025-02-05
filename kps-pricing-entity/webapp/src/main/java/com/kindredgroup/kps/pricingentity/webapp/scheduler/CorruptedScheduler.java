package com.kindredgroup.kps.pricingentity.webapp.scheduler;

import com.kindredgroup.commons.logging.logger.KpsLogger;
import com.kindredgroup.commons.logging.logger.KpsLoggerFactory;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import io.micrometer.core.instrument.Tags;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CorruptedScheduler {
    private final ContestRepository contestRepository;
    private final KpsLogger kpsLogger = KpsLoggerFactory.getLogger(log);
    private List<Long> corruptedContests;

    public CorruptedScheduler(ContestRepository contestRepository, MetricsHelper metricsHelper) {
        this.contestRepository = contestRepository;
        corruptedContests = new ArrayList<>();
        metricsHelper.createGauge("CorruptedData", () -> corruptedContests.size(), Tags.of("domain", "pricing-domain"));
    }

    @Scheduled(fixedDelayString = "${scheduled.corruptedData}", timeUnit = TimeUnit.HOURS)
    @WithSpan
    public void getCorruptedData() {
        corruptedContests = contestRepository.getCorrupted();
        if(corruptedContests.size() > 30) {
            kpsLogger.info("Number of possible corrupted contests: " + corruptedContests.size());
        } else {
            kpsLogger.info("List of possible corrupted contests ids: " + corruptedContests);
        }
    }

}
