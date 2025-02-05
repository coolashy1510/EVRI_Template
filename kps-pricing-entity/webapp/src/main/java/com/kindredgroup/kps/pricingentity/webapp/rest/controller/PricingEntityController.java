package com.kindredgroup.kps.pricingentity.webapp.rest.controller;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PricingEntityController {

    private final Tracer tracer;
    @Autowired
    public PricingEntityController(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer(PricingEntityController.class.getName());
    }

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> test() {
        Span span = tracer.spanBuilder("testEndpoint").startSpan();
        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("OTELTestUP", "200");
            return ResponseEntity.ok("UP");
        } catch (Throwable t){
            span.recordException(t);
            throw t;
        } finally {
            span.end();
        }
    }
}
