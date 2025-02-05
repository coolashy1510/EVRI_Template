package com.kindredgroup.kps.pricingentity.webapp.rest.controller;

import java.util.List;

import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PricingEntityResponse;
import com.kindredgroup.kps.pricingentity.feeddomain.service.OutcomeResultService;
import io.micrometer.core.annotation.Timed;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OutcomeResultController {

    private final OutcomeResultService outcomeResultService;

    public OutcomeResultController(OutcomeResultService outcomeResultService) {
        this.outcomeResultService = outcomeResultService;
    }


    @GetMapping(value = "/outcome", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "OutcomeResultController.getOutcomes"},
           histogram = true)
    public ResponseEntity<PricingEntityResponse> getOutcomes(@RequestParam String contestKey) {
        try {
            List<OutcomeResult> outcomeResult = outcomeResultService.getOutcomeResult(contestKey);
            return ResponseEntity.ok(new PricingEntityResponse(outcomeResult));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        } finally {
            MDC.clear();
        }
    }

}
