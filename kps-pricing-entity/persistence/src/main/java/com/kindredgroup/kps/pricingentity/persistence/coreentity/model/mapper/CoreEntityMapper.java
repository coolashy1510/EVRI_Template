package com.kindredgroup.kps.pricingentity.persistence.coreentity.model.mapper;

import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Competition;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Fixture;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Participant;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Team;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.TeamParticipantsMap;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionAgeCategory;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionType;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import com.kindredgroup.kps.pricingentity.coreentity.domain.FixtureRoundType;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Tournament;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Venue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CoreEntityMapper {

    @NotNull
    public static Competition createCompetition(
            final com.kindredgroup.kps.pricingentity.coreentity.domain.Competition competitionEM) {
        var competition = new Competition();
        competition.setKey(competitionEM.key());
        competition.setName(competitionEM.name());
        competition.setType(CompetitionType.of(competitionEM.competitionType()));
        competition.setAgeCategory(competitionEM.ageCategory() != null ? CompetitionAgeCategory.of(competitionEM.ageCategory()) : CompetitionAgeCategory.UNCONFIRMED);
        competition.setContestType(competitionEM.contestType());
        competition.setGender(competitionEM.gender());
        return competition;
    }

    public static Tournament createTournament(
            final Competition competition,
            @NotNull final com.kindredgroup.kps.pricingentity.coreentity.domain.Tournament tournamentEM) {
        var tournament = new Tournament();
        tournament.setKey(tournamentEM.key());
        tournament.setName(tournamentEM.name());
        tournament.setStartsAt(tournamentEM.startDateTimeUtc());
        tournament.setEndsAt(tournamentEM.endDateTimeUtc());
        if (Objects.nonNull(competition)) {
            tournament.setCompetition(competition);
        }
        return tournament;
    }

    public static Fixture createFixture(
            @NotNull final com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixtureEM,
            final Venue venue,
            final Tournament tournament,
            final List<Team> teams) {
        var fixture = new Fixture();
        fixture.setKey(fixtureEM.key());
        fixture.setName(fixtureEM.name());
        fixture.setContestType(fixtureEM.contestType());
        fixture.setGroupName(fixtureEM.groupName());
        fixture.setStartsAt(fixtureEM.startDateTimeUtc());
        if (Objects.nonNull(fixtureEM.roundType())) {
            fixture.setRoundType(FixtureRoundType.of(fixtureEM.roundType()));
        }
        if (Objects.nonNull(venue)) {
            fixture.setVenue(venue);
        }
        if (Objects.nonNull(tournament)) {
            fixture.setTournament(tournament);
        }
        if (Objects.nonNull(teams) && !teams.isEmpty()) {
            fixture.setTeams(teams);
        }
        return fixture;
    }

    @NotNull
    public static Team createTeam(
            final com.kindredgroup.kps.pricingentity.coreentity.domain.Team teamEM) {
        var team = new Team();
        team.setKey(teamEM.key());
        team.setName(teamEM.name());
        team.setContestType(teamEM.contestType());
        team.setGender(teamEM.gender() != null ? teamEM.gender() : CoreEntityGender.NOT_KNOWN);
        return team;
    }

    @NotNull
    public static Participant createParticipant(
            final com.kindredgroup.kps.pricingentity.coreentity.domain.Participant participantEM) {
        var participant = new Participant();
        participant.setKey(participantEM.key());
        participant.setName(participantEM.name());
        participant.setContestType(participantEM.contestType());
        participant.setDateOfBirth(participantEM.dateOfBirth());
        participant.setGender(participantEM.gender() != null ? participantEM.gender() : CoreEntityGender.NOT_KNOWN);
        return participant;
    }

    @NotNull
    public static Venue createVenue(final com.kindredgroup.kps.pricingentity.coreentity.domain.Venue venueEM) {
        var venue = new Venue();
        venue.setKey(venueEM.key());
        venue.setName(venueEM.name());
        venue.setContestType(venueEM.contestType());
        venue.setCountryCode(venueEM.countryCode());
        return venue;
    }

    @NotNull
    public static com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture getFixtureDto(final Fixture fixture) {
        return com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture.builder()
                .key(fixture.getKey())
                .name(fixture.getName())
                .groupName(fixture.getGroupName())
                .contestType(fixture.getContestType())
                .roundType(fixture.getRoundType() != null ? fixture.getRoundType().getValue() : null)
                .startDateTimeUtc(fixture.getStartsAt())
                .tournament(getTournamentDto(fixture.getTournament()))
                .competition(Optional.ofNullable(fixture.getTournament())
                        .map(tour -> getCompetitionDto(tour.getCompetition()))
                        .orElse(null))
                .teams(getTeamsDto(fixture.getTeams()))
                .venue(getVenueDto(fixture.getVenue()))
                .build();
    }

    public static com.kindredgroup.kps.pricingentity.coreentity.domain.Competition getCompetitionDto(
            final Competition competition) {
        return Optional.ofNullable(competition)
                .map(comp -> com.kindredgroup.kps.pricingentity.coreentity.domain.Competition.builder()
                        .key(comp.getKey())
                        .name(comp.getName())
                        .contestType(comp.getContestType())
                        .competitionType(comp.getType().getValue())
                        .gender(comp.getGender())
                        .ageCategory(competition.getAgeCategory() != null ? competition.getAgeCategory().getValue() : null)
                        .build())
                .orElse(null);
    }

    public static com.kindredgroup.kps.pricingentity.coreentity.domain.Tournament getTournamentDto(
            final Tournament tournament) {
        return Optional.ofNullable(tournament)
                .map(tour -> com.kindredgroup.kps.pricingentity.coreentity.domain.Tournament.builder()
                        .key(tour.getKey())
                        .name(tour.getName())
                        .startDateTimeUtc(tour.getStartsAt())
                        .endDateTimeUtc(tour.getEndsAt())
                        .build())
                .orElse(null);
    }

    public static com.kindredgroup.kps.pricingentity.coreentity.domain.Venue getVenueDto(final Venue venueDb) {
        return Optional.ofNullable(venueDb)
                .map(venue -> com.kindredgroup.kps.pricingentity.coreentity.domain.Venue.builder()
                        .key(venue.getKey())
                        .name(venue.getName())
                        .countryCode(venue.getCountryCode())
                        .contestType(venue.getContestType())
                        .build())
                .orElse(null);
    }

    public static List<com.kindredgroup.kps.pricingentity.coreentity.domain.Team> getTeamsDto(
            final List<Team> teams) {
        return Optional.ofNullable(teams)
                .map(teamList -> teamList.parallelStream()
                        .map(CoreEntityMapper::getTeamDto)
                        .toList())
                .orElseGet(Collections::emptyList);
    }

    @NotNull
    public static com.kindredgroup.kps.pricingentity.coreentity.domain.Team getTeamDto(
            final Team team) {
        return com.kindredgroup.kps.pricingentity.coreentity.domain.Team.builder()
                                                                        .key(team.getKey())
                                                                        .name(team.getName())
                                                                        .contestType(team.getContestType())
                                                                        .participants(getParticipantsDto(
                        team.getTeamParticipantsMaps()))
                                                                        .build();
    }

    public static List<com.kindredgroup.kps.pricingentity.coreentity.domain.Participant> getParticipantsDto(final List<TeamParticipantsMap> teamParticipantsMaps) {
        return Optional.ofNullable(teamParticipantsMaps)
                .map(teamParticipantsMapList -> teamParticipantsMapList.parallelStream()
                        .map(TeamParticipantsMap::getParticipant)
                        .map(CoreEntityMapper::getParticipantDto)
                        .toList())
                .orElse(Collections.emptyList());
    }

    @NotNull
    public static com.kindredgroup.kps.pricingentity.coreentity.domain.Participant getParticipantDto(
            final Participant participant) {
        return com.kindredgroup.kps.pricingentity.coreentity.domain.Participant.builder()
                .key(participant.getKey())
                .name(participant.getName())
                .dateOfBirth(participant.getDateOfBirth())
                .contestType(participant.getContestType())
                .build();
    }
}
