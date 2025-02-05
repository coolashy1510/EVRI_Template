package com.kindredgroup.kps.pricingentity.webapp.rest.controller;

import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture;
import com.kindredgroup.kps.pricingentity.coreentity.service.CoreEntityService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EntityManagerController {

    private final CoreEntityService coreEntityService;


    public EntityManagerController(CoreEntityService coreEntityService) {
        this.coreEntityService = coreEntityService;
    }

    @GetMapping("/fixtures/{key}")
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "EntityManagerController.getFixtures"},
           histogram = true)
    public ResponseEntity<Fixture> getFixtures(@PathVariable @Valid String key) {
        try {
            Fixture fixture = coreEntityService.getFixture(key);
            return ResponseEntity.ok(fixture);
        } catch (Exception e) {
            return ResponseEntity.ok(null);
        } finally {
            MDC.clear();
        }
    }

}
