package com.kindredgroup.kps.pricingentity.webapp.rest.controller;

import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;
import com.kindredgroup.kps.pricingentity.feeddomain.service.OutcomeResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class OutcomeResultControllerTest {
    private OutcomeResultService outcomeResultService;
    private WebTestClient webTestClient;

    @BeforeEach
    public void init() {
        outcomeResultService = mock(OutcomeResultService.class);
        webTestClient = WebTestClient.bindToController(new OutcomeResultController(outcomeResultService)).build();
    }

    @Test
    void getOutcome() {
        when(outcomeResultService.getOutcomeResult("contestKey")).thenReturn(List.of(mock(OutcomeResult.class)));

        webTestClient.get()
                .uri("/outcome?contestKey=contestKey")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("{\"outcomeResultList\":[{\"contestKey\":null,\"outcomeFractions\":[],\"propositionKey\":null,\"source\":null,\"provider\":null,\"timeStampUtc\":null,\"manuallyControlled\":false}]}");
    }

    @Test
    void getOutcome_No_Results() {
        WebTestClient webTestClient = WebTestClient.bindToController(new OutcomeResultController(outcomeResultService)).build();
        when(outcomeResultService.getOutcomeResult("contestKey")).thenReturn(List.of());

        webTestClient.get()
                .uri("/outcome?contestKey=contestKey")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("{\"outcomeResultList\":[]}");
    }

    @Test
    void getOutcome_Not_Found() {
        when(outcomeResultService.getOutcomeResult("contestKey")).thenThrow(IllegalStateException.class);
        webTestClient.get()
                .uri("/outcome?contestKey=contestKey")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

}
