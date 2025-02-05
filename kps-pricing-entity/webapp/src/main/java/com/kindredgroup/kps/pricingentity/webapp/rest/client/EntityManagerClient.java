package com.kindredgroup.kps.pricingentity.webapp.rest.client;

import java.time.Duration;

import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Competition;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Participant;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Team;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Tournament;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Venue;
import com.kindredgroup.kps.pricingentity.coreentity.service.EntityManagerService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.COMPETITION_KEY_NOT_FOUND_REASON_TEXT;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.FIXTURE_KEY_NOT_FOUND_REASON_TEXT;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.PARTICIPANT_KEY_NOT_FOUND_REASON_TEXT;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.TEAM_KEY_NOT_FOUND_REASON_TEXT;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.TOURNAMENT_KEY_NOT_FOUND_REASON_TEXT;
import static com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLoggingReason.VENUE_KEY_NOT_FOUND_REASON_TEXT;

@Component
@Slf4j
public class EntityManagerClient implements EntityManagerService {

    public static final int SECONDS_TIMEOUT = 5;
    public static final int FIXTURE_TIMEOUT = 30;

    private final WebClient webClient;

    public EntityManagerClient(@Qualifier("entityManagerWebClient") WebClient webClient) {
        this.webClient = webClient;
    }


    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "EntityManagerClient.getCompetition"}, histogram = true)
    public Mono<Competition> getCompetition(String competitionKey) {
        WebClient.RequestHeadersSpec<?> uri = webClient.get()
                                                       .uri(uriBuilder -> uriBuilder.path("/Competitions/{competitionKey}")
                                                                                    .build(competitionKey));
        return uri.accept(MediaType.APPLICATION_JSON)
                  .retrieve()
                  .onStatus(
                          httpStatusCode -> !HttpStatus.OK.equals(httpStatusCode),
                          response -> Mono.just(
                                  new NotFoundEntityException(COMPETITION_KEY_NOT_FOUND_REASON_TEXT.getDisplayText())))
                  .bodyToMono(Competition.class)
                  .timeout(Duration.ofSeconds(SECONDS_TIMEOUT));
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "EntityManagerClient.getTournament"}, histogram = true)
    public Mono<Tournament> getTournament(String tournamentKey) {
        WebClient.RequestHeadersSpec<?> uri = webClient.get()
                                                       .uri(uriBuilder -> uriBuilder.path("/Tournaments/{tournamentKey}")
                                                                                    .build(tournamentKey));
        return uri.accept(MediaType.APPLICATION_JSON)
                  .retrieve()
                  .onStatus(
                          httpStatusCode -> !HttpStatus.OK.equals(httpStatusCode),
                          response -> Mono.just(
                                  new NotFoundEntityException(TOURNAMENT_KEY_NOT_FOUND_REASON_TEXT.getDisplayText())))
                  .bodyToMono(Tournament.class)
                  .timeout(Duration.ofSeconds(SECONDS_TIMEOUT));
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "EntityManagerClient.getFixture"}, histogram = true)
    public Mono<Fixture> getFixture(String fixtureKey) {
        WebClient.RequestHeadersSpec<?> uri = webClient.get()
                                                       .uri(uriBuilder -> uriBuilder.path(
                                                                                            "/Fixtures/{fixtureKey}")
                                                                                    .queryParam(
                                                                                            "includeTeamParticipants",
                                                                                            "{includeTeamParticipants}")
                                                                                    .build(fixtureKey, true));
        return uri.accept(MediaType.APPLICATION_JSON)
                  .retrieve()
                  .onStatus(
                          httpStatusCode -> !HttpStatus.OK.equals(httpStatusCode),
                          response -> Mono.just(new NotFoundEntityException(FIXTURE_KEY_NOT_FOUND_REASON_TEXT.getDisplayText())))
                  .bodyToMono(Fixture.class)
                  .timeout(Duration.ofSeconds(FIXTURE_TIMEOUT));
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "EntityManagerClient.getTeam"},
           histogram = true)
    public Mono<Team> getTeam(String teamKey) {
        WebClient.RequestHeadersSpec<?> uri = webClient.get()
                                                       .uri(uriBuilder -> uriBuilder.path("/Teams/{teamKey}")
                                                                                    .build(teamKey));
        return uri.accept(MediaType.APPLICATION_JSON)
                  .retrieve()
                  .onStatus(
                          httpStatusCode -> !HttpStatus.OK.equals(httpStatusCode),
                          response -> Mono.just(new NotFoundEntityException(TEAM_KEY_NOT_FOUND_REASON_TEXT.getDisplayText())))
                  .bodyToMono(Team.class)
                  .timeout(Duration.ofSeconds(SECONDS_TIMEOUT));
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "EntityManagerClient.getParticipant"}, histogram = true)
    public Mono<Participant> getParticipant(String participantKey) {
        WebClient.RequestHeadersSpec<?> uri = webClient.get()
                                                       .uri(uriBuilder -> uriBuilder.path("/Participants/{participantKey}")
                                                                                    .build(participantKey));
        return uri.accept(MediaType.APPLICATION_JSON)
                  .retrieve()
                  .onStatus(
                          httpStatusCode -> !HttpStatus.OK.equals(httpStatusCode),
                          response -> Mono.just(
                                  new NotFoundEntityException(PARTICIPANT_KEY_NOT_FOUND_REASON_TEXT.getDisplayText())))
                  .bodyToMono(Participant.class)
                  .timeout(Duration.ofSeconds(SECONDS_TIMEOUT));
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "EntityManagerClient.getVenue"}, histogram = true)
    public Mono<Venue> getVenue(String venueKey) {
        WebClient.RequestHeadersSpec<?> uri = webClient.get()
                                                       .uri(uriBuilder -> uriBuilder.path("/Venues/{venueKey}")
                                                                                    .build(venueKey));
        return uri.accept(MediaType.APPLICATION_JSON)
                  .retrieve()
                  .onStatus(
                          httpStatusCode -> !HttpStatus.OK.equals(httpStatusCode),
                          response -> Mono.just(new NotFoundEntityException(VENUE_KEY_NOT_FOUND_REASON_TEXT.getDisplayText())))
                  .bodyToMono(Venue.class)
                  .timeout(Duration.ofSeconds(SECONDS_TIMEOUT));
    }

}
