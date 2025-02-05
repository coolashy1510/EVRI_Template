package com.kindredgroup.kps.pricingentity.coreentity.service;

import com.kindredgroup.kps.pricingentity.coreentity.domain.Competition;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Participant;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Team;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Tournament;
import com.kindredgroup.kps.pricingentity.coreentity.domain.Venue;
import reactor.core.publisher.Mono;

public interface EntityManagerService {
    Mono<Competition> getCompetition(String competitionKey);

    Mono<Tournament> getTournament(String tournamentKey);

    Mono<Fixture> getFixture(String fixtureKey);

    Mono<Team> getTeam(String teamKey);

    Mono<Participant> getParticipant(String participantKey);

    Mono<Venue> getVenue(String venueKey);
}
