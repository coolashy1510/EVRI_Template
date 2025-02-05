package com.kindredgroup.kps.pricingentity.persistence.coreentity.service;

import com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLogger;
import com.kindredgroup.kps.pricingentity.coreentity.service.CoreEntityService;
import com.kindredgroup.kps.pricingentity.coreentity.service.EntityManagerService;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Participant;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Team;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.TeamParticipantsMap;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Tournament;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.mapper.CoreEntityMapper;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Competition;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Venue;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.repository.CompetitionRepository;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.repository.FixtureRepository;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.repository.ParticipantRepository;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.repository.TeamParticipantMapRepository;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.repository.TeamRepository;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.repository.TournamentRepository;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.repository.VenueRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CoreEntityServiceImpl implements CoreEntityService {
    private final EntityManagerService entityManagerService;
    private final CompetitionRepository competitionRepository;
    private final TournamentRepository tournamentRepository;
    private final FixtureRepository fixtureRepository;
    private final TeamRepository teamRepository;
    private final ParticipantRepository participantRepository;
    private final TeamParticipantMapRepository teamParticipantMapRepository;
    private final VenueRepository venueRepository;
    private final CoreEntityLogger logger;

    @Autowired
    public CoreEntityServiceImpl(
            EntityManagerService entityManagerService,
            CompetitionRepository competitionRepository,
            TournamentRepository tournamentRepository,
            FixtureRepository fixtureRepository,
            TeamRepository teamRepository,
            ParticipantRepository participantRepository,
            TeamParticipantMapRepository teamParticipantMapRepository,
            VenueRepository venueRepository,
            CoreEntityLogger coreEntityLogger) {
        this.entityManagerService = entityManagerService;
        this.competitionRepository = competitionRepository;
        this.tournamentRepository = tournamentRepository;
        this.fixtureRepository = fixtureRepository;
        this.teamRepository = teamRepository;
        this.participantRepository = participantRepository;
        this.teamParticipantMapRepository = teamParticipantMapRepository;
        logger = coreEntityLogger;
        this.venueRepository = venueRepository;
    }

    private TeamParticipantsMap createTeamParticipantMap(
            final Participant participant,
            final Team team,
            final Tournament tournament) {
        var teamParticipantsMap = new TeamParticipantsMap();
        teamParticipantsMap.setParticipantId(participant.getId());
        teamParticipantsMap.setTournamentId(tournament.getId());
        teamParticipantsMap.setParticipant(participant);
        teamParticipantsMap.setTeam(team);
        teamParticipantsMap.setTournament(tournament);
        return teamParticipantsMap;
    }

    @Override
    public com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture getFixture(String fixtureKey) {
        return fixtureRepository.findByKey(fixtureKey)
                .map(CoreEntityMapper::getFixtureDto)
                .orElseGet(() -> entityManagerService.getFixture(fixtureKey)
                        .doOnError(err -> logger.logFixtureNotFoundWarning(fixtureKey))
                        .blockOptional()
                        .map(
                                fixture -> {
                                    final var competition = getCompetition(fixture);
                                    final var tournament = getTournament(fixture,
                                            competition);
                                    final var teams = getTeams(fixture, tournament);
                                    saveTeamParticipantsMap(teams);
                                    final var venue = getVenue(fixture);
                                    return fixtureRepository.save(
                                            CoreEntityMapper.createFixture(fixture,
                                                    venue, tournament, teams));
                                })
                        .map(CoreEntityMapper::getFixtureDto)
                        .orElseGet(
                                () -> {
                                    logger.logFixtureNotFoundWarning(fixtureKey);
                                    return null;
                                })

                );
    }

    private void saveTeamParticipantsMap(List<Team> teams) {
        teams.forEach(team -> {
            team.getTeamParticipantsMaps().forEach(tpm -> tpm.setTeamId(team.getId()));
            teamParticipantMapRepository.saveAll(team.getTeamParticipantsMaps());
        });
    }

    private Competition getCompetition(
            @NotNull final com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixture) {
        if (Objects.nonNull(fixture.competition())) {
            return competitionRepository.findByKey(fixture.competition().key())
                    .orElseGet(() -> entityManagerService.getCompetition(fixture.competition().key())
                            .blockOptional()
                            .map(CoreEntityMapper::createCompetition)
                            .map(competitionRepository::save)
                            .orElse(null)
                    );
        } else {
            logger.logCompetitionNotFoundWarning(fixture);
            return null;
        }
    }

    private Tournament getTournament(
            @NotNull final com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixture,
            final Competition competition) {
        if (Objects.nonNull(fixture.tournament())) {
            return tournamentRepository.findByKey(fixture.tournament().key())
                    .orElseGet(() -> entityManagerService.getTournament(fixture.tournament().key())
                            .blockOptional()
                            .map(tournament -> CoreEntityMapper.createTournament(
                                    competition, tournament))
                            .map(tournamentRepository::save)
                            .orElse(null)
                    );
        } else {
            logger.logTournamentNotFoundWarning(fixture);
            return null;
        }
    }

    private List<Team> getTeams(@NotNull final com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixture, Tournament tournament) {
        if (Objects.nonNull(fixture.teams()) && !fixture.teams().isEmpty()) {
            return fixture.teams()
                    .parallelStream()
                    .map(team -> teamRepository.findByKey(team.key())
                            .orElseGet(() -> entityManagerService.getTeam(team.key())
                                    .blockOptional()
                                    .map(CoreEntityMapper::createTeam)
                                    .map(teamEntity ->
                                            addParticipantsFromFixture(fixture.teams(), team.participants(), teamEntity, tournament)
                                    )
                                    .map(teamRepository::save)
                                    .orElse(null)))
                    .filter(Objects::nonNull)
                    .toList();
        } else {
            logger.logTeamsNotFoundWarning(fixture);
            return Collections.emptyList();
        }
    }

    private List<TeamParticipantsMap> getTeamParticipantMapList(final List<com.kindredgroup.kps.pricingentity.coreentity.domain.Participant> participants, Team teamEntity, Tournament tournament) {
        return Optional.ofNullable(participants)
                .map(list -> list.stream()
                        .map(participantEM ->
                                participantRepository.findByKey(participantEM.key())
                                        .map(participant -> createTeamParticipantMap(participant, teamEntity, tournament))
                                        .orElseGet(() -> entityManagerService.getParticipant(
                                                        participantEM.key())
                                                .blockOptional()
                                                .map(CoreEntityMapper::createParticipant)
                                                .map(participantRepository::save)
                                                .map(participant -> createTeamParticipantMap(participant, teamEntity, tournament))
                                                .orElse(null)))
                        .filter(Objects::nonNull)
                        .toList())

                .orElse(Collections.emptyList());
    }

    private Venue getVenue(final com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixture) {
        if (Objects.nonNull(fixture.venue())) {
            return venueRepository.findByKey(fixture.venue().key())
                    .orElseGet(() -> entityManagerService.getVenue(fixture.venue().key())
                            .blockOptional()
                            .map(CoreEntityMapper::createVenue)
                            .map(venueRepository::save)
                            .orElse(null)
                    );
        } else {
            logger.logVenueNotFoundWarning(fixture);
            return null;
        }
    }

    private Team addParticipantsFromFixture(List<com.kindredgroup.kps.pricingentity.coreentity.domain.Team> teams, final List<com.kindredgroup.kps.pricingentity.coreentity.domain.Participant> participants,
                                            @NotNull Team teamEntity,
                                            Tournament tournament) {
        if (!participants.isEmpty()) {
            teamEntity.setTeamParticipantsMaps(getTeamParticipantMapList(participants, teamEntity, tournament));
        } else if (!teams.isEmpty()) {
            teams.forEach(team -> teamEntity.getTeamParticipantsMaps().addAll(getTeamParticipantMapList(participants, teamEntity, tournament)));
        }

        return teamEntity;
    }
}

