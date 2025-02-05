package com.kindredgroup.kps.pricingentity.webapp.rest.controller;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.Tracer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Testing Pricing Entity Controller")
@ExtendWith(SpringExtension.class)
class PricingEntityControllerTest {
    @Mock
    private OpenTelemetry openTelemetry;
    @Mock
    private Tracer tracer;

    @Test
    void test() {
        when(openTelemetry.getTracer(anyString())).thenReturn(tracer);
        when(tracer.spanBuilder(anyString())).thenReturn(mock(SpanBuilder.class));
        when(tracer.spanBuilder(anyString()).startSpan()).thenReturn(mock(Span.class));
        WebTestClient webTestClient = WebTestClient.bindToController(new PricingEntityController(openTelemetry)).build();

        webTestClient.get()
                .uri("/test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("UP");
    }
}
