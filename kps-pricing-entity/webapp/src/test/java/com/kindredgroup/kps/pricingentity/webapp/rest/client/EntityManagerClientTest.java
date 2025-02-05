package com.kindredgroup.kps.pricingentity.webapp.rest.client;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Competition;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Participant;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Team;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Tournament;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Venue;
import com.kindredgroup.kps.pricingentity.webapp.rest.client.config.EntityManagerClientConfig;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import okhttp3.Headers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Import(EntityManagerClientConfig.class)
class EntityManagerClientTest {
    static MockWebServer mockBackEnd;
    WebClient webClient;
    EntityManagerClient client;
    ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        webClient = WebClient.builder()
                             .baseUrl(baseUrl)
                             .build();
        client = new EntityManagerClient(webClient);
    }

    @Test
    void getCompetitionSuccess() throws JsonProcessingException {
        Competition mockCompetition = Competition.builder()
                                                 .contestType("contestType")
                                                 .competitionType("competitionType")
                                                 .build();
        mockBackEnd.enqueue(new MockResponse(200, new Headers(
                new String[]{"Content-Type", "application/json"}), mapper.writeValueAsString(mockCompetition)));
        Mono<Competition> competitionMono = client.getCompetition("1");
        StepVerifier.create(competitionMono)
                    .assertNext(
                            competition -> assertAll(
                                    () -> assertNotNull(competition),
                                    () -> Assertions.assertThat(competition.contestType()).isEqualTo(mockCompetition.contestType()),
                                    () -> Assertions.assertThat(competition.competitionType()).isEqualTo(mockCompetition.competitionType())
                            )
                    )
                    .verifyComplete();

    }

    @Test
    void getCompetitionFail() {
        mockBackEnd.enqueue(new MockResponse(400, new Headers(new String[]{"Content-Type", "application/json"})));
        Mono<Competition> competitionMono = client.getCompetition("1");
        StepVerifier.create(competitionMono)
                    .expectError(NotFoundEntityException.class)
                    .verify();
    }

    @Test
    void getTournamentSuccess() throws JsonProcessingException {
        Tournament mockTournament = Tournament.builder()
                                              .key("key")
                                              .name("name")
                                              .build();
        mockBackEnd.enqueue(new MockResponse(200, new Headers(
                new String[]{"Content-Type", "application/json"}), mapper.writeValueAsString(mockTournament)));
        Mono<Tournament> tournamentMono = client.getTournament("1");
        StepVerifier.create(tournamentMono)
                    .assertNext(
                            tournament -> assertAll(
                                    () -> assertNotNull(tournament),
                                    () -> Assertions.assertThat(tournament.key()).isEqualTo(mockTournament.key()),
                                    () -> Assertions.assertThat(tournament.name()).isEqualTo(mockTournament.name())
                            )
                    )
                    .verifyComplete();

    }

    @Test
    void getTournamentFail() {
        mockBackEnd.enqueue(new MockResponse(404, new Headers(new String[]{"Content-Type", "application/json"})));
        Mono<Tournament> tournamentMono = client.getTournament("1");
        StepVerifier.create(tournamentMono)
                    .expectError(NotFoundEntityException.class)
                    .verify();

    }

    @Test
    void getFixtureSuccess() throws JsonProcessingException {
        Fixture mockFixture = Fixture.builder()
                                     .key("key")
                                     .name("name")
                                     .build();
        mockBackEnd.enqueue(new MockResponse(200, new Headers(
                new String[]{"Content-Type", "application/json"}), mapper.writeValueAsString(mockFixture)));
        Mono<Fixture> fixtureMono = client.getFixture("1");
        StepVerifier.create(fixtureMono)
                    .assertNext(
                            fixture -> assertAll(
                                    () -> assertNotNull(fixture),
                                    () -> Assertions.assertThat(fixture.key()).isEqualTo(mockFixture.key()),
                                    () -> Assertions.assertThat(fixture.name()).isEqualTo(mockFixture.name())
                            )
                    )
                    .verifyComplete();

    }

    @Test
    void getFixtureFail() {
        mockBackEnd.enqueue(new MockResponse(404, new Headers(new String[]{"Content-Type", "application/json"})));
        Mono<Fixture> fixtureMono = client.getFixture("1");
        StepVerifier.create(fixtureMono)
                    .expectError(NotFoundEntityException.class)
                    .verify();

    }

    @Test
    void getTeamSuccess() throws JsonProcessingException {
        Team mockTeam = Team.builder()
                            .key("key")
                            .name("name")
                            .build();
        mockBackEnd.enqueue(new MockResponse(200, new Headers(
                new String[]{"Content-Type", "application/json"}), mapper.writeValueAsString(mockTeam)));
        Mono<Team> teamMono = client.getTeam("1");
        StepVerifier.create(teamMono)
                    .assertNext(
                            team -> assertAll(
                                    () -> assertNotNull(team),
                                    () -> Assertions.assertThat(team.key()).isEqualTo(mockTeam.key()),
                                    () -> Assertions.assertThat(team.name()).isEqualTo(mockTeam.name())
                            )
                    )
                    .verifyComplete();

    }

    @Test
    void getTeamFail() {
        mockBackEnd.enqueue(new MockResponse(404, new Headers(new String[]{"Content-Type", "application/json"})));
        Mono<Team> teamMono = client.getTeam("1");
        StepVerifier.create(teamMono)
                    .expectError(NotFoundEntityException.class)
                    .verify();

    }

    @Test
    void getParticipantSuccess() throws JsonProcessingException {
        Participant mockParticipant = Participant.builder()
                                                 .key("key")
                                                 .name("name")
                                                 .build();
        mockBackEnd.enqueue(new MockResponse(200, new Headers(
                new String[]{"Content-Type", "application/json"}), mapper.writeValueAsString(mockParticipant)));
        Mono<Participant> participantMono = client.getParticipant("1");
        StepVerifier.create(participantMono)
                    .assertNext(
                            participant -> assertAll(
                                    () -> assertNotNull(participant),
                                    () -> Assertions.assertThat(participant.key()).isEqualTo(mockParticipant.key()),
                                    () -> Assertions.assertThat(participant.name()).isEqualTo(mockParticipant.name())
                            )
                    )
                    .verifyComplete();

    }

    @Test
    void getParticipantFail() {
        mockBackEnd.enqueue(new MockResponse(404, new Headers(new String[]{"Content-Type", "application/json"})));
        Mono<Participant> participantMono = client.getParticipant("1");
        StepVerifier.create(participantMono)
                    .expectError(NotFoundEntityException.class)
                    .verify();

    }

    @Test
    void getVenueSuccess() throws JsonProcessingException {
        Venue mockVenue = Venue.builder()
                               .key("key")
                               .name("name")
                               .build();
        mockBackEnd.enqueue(new MockResponse(200, new Headers(
                new String[]{"Content-Type", "application/json"}), mapper.writeValueAsString(mockVenue)));
        Mono<Venue> venueMono = client.getVenue("1");
        StepVerifier.create(venueMono)
                    .assertNext(
                            venue -> assertAll(
                                    () -> assertNotNull(venue),
                                    () -> Assertions.assertThat(venue.key()).isEqualTo(mockVenue.key()),
                                    () -> Assertions.assertThat(venue.name()).isEqualTo(mockVenue.name())
                            )
                    )
                    .verifyComplete();

    }

    @Test
    void getVenueFail() {
        mockBackEnd.enqueue(new MockResponse(404, new Headers(new String[]{"Content-Type", "application/json"})));
        Mono<Venue> venueMono = client.getVenue("1");
        StepVerifier.create(venueMono)
                    .expectError(NotFoundEntityException.class)
                    .verify();

    }
}
