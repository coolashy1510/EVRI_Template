package com.kindredgroup.kps.pricingentity.persistence.coreentity.service;

import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionAgeCategory;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionType;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import com.kindredgroup.kps.pricingentity.coreentity.domain.FixtureRoundType;
import com.kindredgroup.kps.pricingentity.coreentity.logging.CoreEntityLogger;
import com.kindredgroup.kps.pricingentity.coreentity.service.CoreEntityService;
import com.kindredgroup.kps.pricingentity.coreentity.service.EntityManagerService;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.*;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CoreEntityServiceImplTest {
    private static final String VALID_FIXTURE_KEY = "valid_fixture_key";
    private static final String FIXTURE_NAME = "fixture_name";
    private static final String FIXTURE_GROUP_NAME = "fixture_group_name";
    private static final OffsetDateTime FIXTURE_STARTING_DATE = OffsetDateTime.now();
    private static final FixtureRoundType FIXTURE_ROUND_TYPE = FixtureRoundType.PRELIMINARY;
    private static final String VALID_TOURNAMENT_KEY = "valid_tournament_key";
    private static final String TOURNAMENT_NAME = "tournament_name";
    private static final OffsetDateTime TOURNAMENT_STARTING_DATE = OffsetDateTime.now();
    private static final OffsetDateTime TOURNAMENT_ENDING_DATE = OffsetDateTime.now().plus(7L, ChronoUnit.DAYS);
    private static final String VALID_COMPETITION_KEY = "valid_competition_key";
    private static final String COMPETITION_NAME = "competition_name";
    private static final String CONTEST_TYPE = "contest_type";
    private static final CompetitionType COMPETITION_TYPE = CompetitionType.LEAGUE;
    private static final CoreEntityGender GENDER = CoreEntityGender.MIXED;
    private static final CompetitionAgeCategory COMPETITION_AGE_CATEGORY = CompetitionAgeCategory.SENIOR;
    private static final String VALID_TEAM_KEY = "valid_team_key";
    private static final String TEAM_NAME = "team_name";
    private static final String VALID_VENUE_KEY = "valid_venue_key";
    private static final String VENUE_NAME = "venue_name";
    private static final String COUNTRY_CODE = "country_code";
    private static final String VALID_PARTICIPANT_KEY = "valid_participant_key";
    private static final String PARTICIPANT_NAME = "participant_name";
    private static final OffsetDateTime PARTICIPANT_DATE_OF_BIRTH = OffsetDateTime.now();
    @Mock
    private EntityManagerService entityManagerService;
    @Mock
    private CompetitionRepository competitionRepository;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private FixtureRepository fixtureRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private TeamParticipantMapRepository teamParticipantMapRepository;
    @Mock
    private VenueRepository venueRepository;
    @Mock
    private CoreEntityLogger logger;
    private CoreEntityService service;

    @Captor
    private ArgumentCaptor<Fixture> fixtureCaptor;
    @Captor
    private ArgumentCaptor<Competition> competitionCaptor;
    @Captor
    private ArgumentCaptor<Tournament> tournamentCaptor;
    @Captor
    private ArgumentCaptor<Venue> venueCaptor;
    @Captor
    private ArgumentCaptor<Team> teamCaptor;
    @Captor
    private ArgumentCaptor<Participant> participantCaptor;

    AutoCloseable closeable;

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new CoreEntityServiceImpl(
                entityManagerService,
                competitionRepository,
                tournamentRepository,
                fixtureRepository,
                teamRepository,
                participantRepository,
                teamParticipantMapRepository,
                venueRepository,
                logger);
    }

    @AfterEach
    void terminate() throws Exception {
        closeable.close();
    }

    @Test
    void getSavedFixture() {
        Fixture fixtureMock = getMockFixtureDb();
        when(fixtureRepository.findByKey(VALID_FIXTURE_KEY))
                .thenReturn(Optional.of(fixtureMock));
        com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixtureResponse = service.getFixture(VALID_FIXTURE_KEY);
        verify(fixtureRepository, times(1)).findByKey(VALID_FIXTURE_KEY);
        verifyFixture(fixtureResponse);
    }

    @Test
    void getSavedFixtureWithTournament() {
        Fixture fixtureMock = getMockFixtureDb();
        fixtureMock.setTournament(getMockTournamentDb());
        when(fixtureRepository.findByKey(VALID_FIXTURE_KEY))
                .thenReturn(Optional.of(fixtureMock));
        com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixtureResponse = service.getFixture(VALID_FIXTURE_KEY);
        verify(fixtureRepository, times(1)).findByKey(VALID_FIXTURE_KEY);
        verifyFixture(fixtureResponse);
        verifyTournament(fixtureResponse.tournament());
    }

    @Test
    void getSavedFixtureWithTournamentAndCompetition() {
        Fixture fixtureMock = getMockFixtureDb();
        Tournament tournamentMock = getMockTournamentDb();
        tournamentMock.setCompetition(getMockCompetitionDb());
        fixtureMock.setTournament(tournamentMock);
        when(fixtureRepository.findByKey(VALID_FIXTURE_KEY))
                .thenReturn(Optional.of(fixtureMock));
        com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixtureResponse = service.getFixture(VALID_FIXTURE_KEY);
        verify(fixtureRepository, times(1)).findByKey(VALID_FIXTURE_KEY);
        verifyFixture(fixtureResponse);
        verifyTournament(fixtureResponse.tournament());
        verifyCompetition(fixtureResponse.competition());
    }

    @Test
    void getSavedFixtureWithTournamentAndCompetitionAndTeams() {
        var fixtureMock = getMockFixtureDb();
        var tournamentMock = getMockTournamentDb();
        var mockTeamsDb = getMockTeamsDb();
        var mockTeamParticipantsMap = getMockTeamParticipantsMap();
        tournamentMock.setCompetition(getMockCompetitionDb());
        fixtureMock.setTournament(tournamentMock);
        mockTeamParticipantsMap.setParticipant(getMockParticipantDb());
        mockTeamsDb.setTeamParticipantsMaps(List.of(mockTeamParticipantsMap));
        fixtureMock.setTeams(List.of(mockTeamsDb));
        when(fixtureRepository.findByKey(VALID_FIXTURE_KEY))
                .thenReturn(Optional.of(fixtureMock));
        var fixtureResponse = service.getFixture(VALID_FIXTURE_KEY);
        verify(fixtureRepository, times(1)).findByKey(VALID_FIXTURE_KEY);
        verifyFixture(fixtureResponse);
        verifyTournament(fixtureResponse.tournament());
        verifyCompetition(fixtureResponse.competition());
        verifyTeams(fixtureResponse.teams());
    }

    @Test
    void getFixtureFail() {
        when(fixtureRepository.findByKey(VALID_FIXTURE_KEY))
                .thenReturn(Optional.empty());
        when(entityManagerService.getFixture(VALID_FIXTURE_KEY))
                .thenReturn(Mono.empty());
        com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixtureResponse = service.getFixture(VALID_FIXTURE_KEY);
        assertNull(fixtureResponse);
    }

    @Test
    void getFixtureFromEM() {
        Fixture fixtureDbMock = getMockFixtureDb();
        var fixtureEMMock = com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture.builder()
                .key(VALID_FIXTURE_KEY)
                .name(FIXTURE_NAME)
                .groupName(FIXTURE_GROUP_NAME)
                .contestType(CONTEST_TYPE)
                .roundType(
                        FIXTURE_ROUND_TYPE.getValue())
                .startDateTimeUtc(
                        FIXTURE_STARTING_DATE)
                .teams(Collections.emptyList())
                .build();
        when(fixtureRepository.findByKey(VALID_FIXTURE_KEY))
                .thenReturn(Optional.empty());
        when(entityManagerService.getFixture(VALID_FIXTURE_KEY))
                .thenReturn(Mono.just(fixtureEMMock));
        when(fixtureRepository.save(fixtureCaptor.capture())).thenReturn(fixtureDbMock);
        com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixtureResponse = service.getFixture(VALID_FIXTURE_KEY);
        verifyLogs(fixtureEMMock);
        verifyFixture(fixtureCaptor.getValue());
        verifyFixture(fixtureResponse);
    }

    private void verifyLogs(com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixtureEMMock) {
        verify(logger).logCompetitionNotFoundWarning(eq(fixtureEMMock));
        verify(logger).logTournamentNotFoundWarning(eq(fixtureEMMock));
        verify(logger).logTeamsNotFoundWarning(eq(fixtureEMMock));
        verify(logger).logVenueNotFoundWarning(eq(fixtureEMMock));
    }

    @Test
    void getFullFixtureFromEM() {
        var fixtureDbMock = getMockFixtureDb();
        var competitionDbMock = getMockCompetitionDb();
        var tournamentDbMock = getMockTournamentDb();
        var venueDbMock = getMockVenueDb();
        var teamDbMock = getMockTeamsDb();
        var teamParticipantsMapDbMock = getMockTeamParticipantsMap();
        var participantDbMock = getMockParticipantDb();
        teamDbMock.setHomeVenue(venueDbMock);
        teamParticipantsMapDbMock.setParticipant(participantDbMock);
        teamDbMock.setTeamParticipantsMaps(List.of(teamParticipantsMapDbMock));
        tournamentDbMock.setCompetition(competitionDbMock);
        fixtureDbMock.setTournament(tournamentDbMock);
        fixtureDbMock.setVenue(venueDbMock);
        fixtureDbMock.setTeams(List.of(teamDbMock));
        var competitionEMMock = com.kindredgroup.kps.pricingentity.coreentity.domain.Competition.builder()
                .key(VALID_COMPETITION_KEY)
                .name(COMPETITION_NAME)
                .contestType(CONTEST_TYPE)
                .competitionType(
                        COMPETITION_TYPE.getValue())
                .gender(GENDER)
                .ageCategory(
                        COMPETITION_AGE_CATEGORY.getValue())
                .build();
        var tournamentEMMock = com.kindredgroup.kps.pricingentity.coreentity.domain.Tournament.builder()
                .key(VALID_TOURNAMENT_KEY)
                .name(TOURNAMENT_NAME)
                .competition(competitionEMMock)
                .build();
        var venueEMMock = com.kindredgroup.kps.pricingentity.coreentity.domain.Venue.builder().key(VALID_VENUE_KEY)
                .name(VENUE_NAME)
                .contestType(CONTEST_TYPE)
                .countryCode(COUNTRY_CODE)
                .build();
        var participantEMMock = com.kindredgroup.kps.pricingentity.coreentity.domain.Participant.builder()
                .key(VALID_PARTICIPANT_KEY)
                .name(PARTICIPANT_NAME)
                .contestType(CONTEST_TYPE)
                .dateOfBirth(
                        PARTICIPANT_DATE_OF_BIRTH)
                .build();
        var teamEMMock = com.kindredgroup.kps.pricingentity.coreentity.domain.Team.builder()
                .key(VALID_TEAM_KEY)
                .name(TEAM_NAME)
                .contestType(CONTEST_TYPE)
                .participants(List.of(participantEMMock))
                .build();
        var fixtureEMMock = com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture.builder()
                .key(VALID_FIXTURE_KEY)
                .name(FIXTURE_NAME)
                .groupName(FIXTURE_GROUP_NAME)
                .contestType(CONTEST_TYPE)
                .roundType(
                        FIXTURE_ROUND_TYPE.getValue())
                .startDateTimeUtc(
                        FIXTURE_STARTING_DATE)
                .teams(List.of(teamEMMock))
                .competition(competitionEMMock)
                .tournament(tournamentEMMock)
                .venue(venueEMMock)
                .build();

        when(fixtureRepository.findByKey(VALID_FIXTURE_KEY))
                .thenReturn(Optional.empty());
        when(entityManagerService.getFixture(VALID_FIXTURE_KEY))
                .thenReturn(Mono.just(fixtureEMMock));
        when(competitionRepository.findByKey(VALID_COMPETITION_KEY))
                .thenReturn(Optional.empty());
        when(entityManagerService.getCompetition(VALID_COMPETITION_KEY))
                .thenReturn(Mono.just(competitionEMMock));
        when(competitionRepository.save(competitionCaptor.capture()))
                .thenReturn(competitionDbMock);
        when(tournamentRepository.findByKey(VALID_TOURNAMENT_KEY))
                .thenReturn(Optional.empty());
        when(entityManagerService.getTournament(VALID_TOURNAMENT_KEY))
                .thenReturn(Mono.just(tournamentEMMock));
        when(tournamentRepository.save(tournamentCaptor.capture()))
                .thenReturn(tournamentDbMock);
        when(venueRepository.findByKey(VALID_VENUE_KEY))
                .thenReturn(Optional.empty());
        when(entityManagerService.getVenue(VALID_VENUE_KEY))
                .thenReturn(Mono.just(venueEMMock));
        when(venueRepository.save(venueCaptor.capture()))
                .thenReturn(venueDbMock);
        when(teamRepository.findByKey(VALID_TEAM_KEY))
                .thenReturn(Optional.empty());
        when(entityManagerService.getTeam(VALID_TEAM_KEY))
                .thenReturn(Mono.just(teamEMMock));
        when(teamRepository.save(teamCaptor.capture()))
                .thenReturn(teamDbMock);
        when(participantRepository.findByKey(VALID_PARTICIPANT_KEY))
                .thenReturn(Optional.empty());
        when(entityManagerService.getParticipant(VALID_PARTICIPANT_KEY))
                .thenReturn(Mono.just(participantEMMock));
        when(participantRepository.save(participantCaptor.capture()))
                .thenReturn(participantDbMock);
        when(fixtureRepository.save(fixtureCaptor.capture()))
                .thenReturn(fixtureDbMock);

        com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixtureResponse = service.getFixture(VALID_FIXTURE_KEY);

        verifyFixture(fixtureCaptor.getValue());
        verifyCompetition(competitionCaptor.getValue());
        verifyTournament(tournamentCaptor.getValue());
        verifyVenue(venueCaptor.getValue());
        verifyTeam(teamCaptor.getValue());
        verifyParticipant(participantCaptor.getValue());
        verifyFixture(fixtureResponse);
        verifyCompetition(fixtureResponse.competition());
        verifyTournament(fixtureResponse.tournament());
        verifyVenue(fixtureResponse.venue());
        verifyTeams(fixtureResponse.teams());
    }

    private Team getMockTeamsDb() {
        Team team = new Team();
        team.setKey(VALID_TEAM_KEY);
        team.setName(TEAM_NAME);
        team.setContestType(CONTEST_TYPE);
        return team;
    }

    private TeamParticipantsMap getMockTeamParticipantsMap() {
        return new TeamParticipantsMap();
    }

    private Participant getMockParticipantDb() {
        Participant participant = new Participant();
        participant.setKey(VALID_PARTICIPANT_KEY);
        participant.setName(PARTICIPANT_NAME);
        participant.setContestType(CONTEST_TYPE);
        participant.setDateOfBirth(PARTICIPANT_DATE_OF_BIRTH);
        return participant;
    }

    private Venue getMockVenueDb() {
        Venue venue = new Venue();
        venue.setKey(VALID_VENUE_KEY);
        venue.setName(VENUE_NAME);
        venue.setContestType(CONTEST_TYPE);
        venue.setCountryCode(COUNTRY_CODE);
        return venue;
    }

    private void verifyVenue(final Venue venue) {
        assertTrue(Objects.nonNull(venue));
        assertAll(
                () -> assertEquals(VALID_VENUE_KEY, venue.getKey()),
                () -> assertEquals(VENUE_NAME, venue.getName()),
                () -> assertEquals(CONTEST_TYPE, venue.getContestType()),
                () -> assertEquals(COUNTRY_CODE, venue.getCountryCode())
        );
    }

    private Competition getMockCompetitionDb() {
        Competition competition = new Competition();
        competition.setKey(VALID_COMPETITION_KEY);
        competition.setName(COMPETITION_NAME);
        competition.setContestType(CONTEST_TYPE);
        competition.setType(COMPETITION_TYPE);
        competition.setGender(GENDER);
        competition.setAgeCategory(COMPETITION_AGE_CATEGORY);
        return competition;
    }

    private Tournament getMockTournamentDb() {
        Tournament tournament = new Tournament();
        tournament.setKey(VALID_TOURNAMENT_KEY);
        tournament.setName(TOURNAMENT_NAME);
        tournament.setStartsAt(TOURNAMENT_STARTING_DATE);
        tournament.setEndsAt(TOURNAMENT_ENDING_DATE);
        return tournament;
    }

    private static Fixture getMockFixtureDb() {
        Fixture fixtureMock = new Fixture();
        fixtureMock.setKey(VALID_FIXTURE_KEY);
        fixtureMock.setName(FIXTURE_NAME);
        fixtureMock.setGroupName(FIXTURE_GROUP_NAME);
        fixtureMock.setContestType(CONTEST_TYPE);
        fixtureMock.setStartsAt(FIXTURE_STARTING_DATE);
        fixtureMock.setRoundType(FIXTURE_ROUND_TYPE);
        return fixtureMock;
    }

    private static void verifyParticipant(final Participant participant) {
        assertTrue(Objects.nonNull(participant));
        assertAll(
                () -> assertEquals(VALID_PARTICIPANT_KEY, participant.getKey()),
                () -> assertEquals(PARTICIPANT_NAME, participant.getName()),
                () -> assertEquals(CONTEST_TYPE, participant.getContestType()),
                () -> assertEquals(PARTICIPANT_DATE_OF_BIRTH, participant.getDateOfBirth())
        );
    }

    private static void verifyTeam(final Team team) {
        assertTrue(Objects.nonNull(team));
        assertAll(
                () -> assertEquals(VALID_TEAM_KEY, team.getKey()),
                () -> assertEquals(TEAM_NAME, team.getName()),
                () -> assertEquals(CONTEST_TYPE, team.getContestType()),
                () -> assertEquals(VALID_PARTICIPANT_KEY, team.getTeamParticipantsMaps().get(0).getParticipant().getKey()),
                () -> assertEquals(PARTICIPANT_NAME, team.getTeamParticipantsMaps().get(0).getParticipant().getName()),
                () -> assertEquals(CONTEST_TYPE, team.getTeamParticipantsMaps().get(0).getParticipant().getContestType()),
                () -> assertEquals(PARTICIPANT_DATE_OF_BIRTH,
                        team.getTeamParticipantsMaps().get(0).getParticipant().getDateOfBirth())
        );
    }

    private static void verifyTeams(final List<com.kindredgroup.kps.pricingentity.coreentity.domain.Team> teams) {
        assertTrue(Objects.nonNull(teams));
        assertAll(
                () -> assertEquals(VALID_TEAM_KEY, teams.get(0).key()),
                () -> assertEquals(TEAM_NAME, teams.get(0).name()),
                () -> assertEquals(CONTEST_TYPE, teams.get(0).contestType()),
                () -> assertEquals(VALID_PARTICIPANT_KEY, teams.get(0).participants().get(0).key()),
                () -> assertEquals(PARTICIPANT_NAME, teams.get(0).participants().get(0).name()),
                () -> assertEquals(CONTEST_TYPE, teams.get(0).participants().get(0).contestType()),
                () -> assertEquals(PARTICIPANT_DATE_OF_BIRTH, teams.get(0).participants().get(0).dateOfBirth())
        );
    }

    private static void verifyCompetition(final Competition competition) {
        assertTrue(Objects.nonNull(competition));
        assertAll(
                () -> assertEquals(VALID_COMPETITION_KEY, competition.getKey()),
                () -> assertEquals(COMPETITION_NAME, competition.getName()),
                () -> assertEquals(CONTEST_TYPE, competition.getContestType()),
                () -> assertEquals(GENDER.getValue(), competition.getGender().getValue()),
                () -> assertEquals(COMPETITION_TYPE.getValue(), competition.getType().getValue()),
                () -> assertEquals(COMPETITION_AGE_CATEGORY.getValue(), competition.getAgeCategory().getValue())
        );
    }

    private static void verifyCompetition(
            final com.kindredgroup.kps.pricingentity.coreentity.domain.Competition competition) {
        assertTrue(Objects.nonNull(competition));
        assertAll(
                () -> assertEquals(VALID_COMPETITION_KEY, competition.key()),
                () -> assertEquals(COMPETITION_NAME, competition.name()),
                () -> assertEquals(CONTEST_TYPE, competition.contestType()),
                () -> assertEquals(GENDER, competition.gender()),
                () -> assertEquals(COMPETITION_TYPE.getValue(), competition.competitionType()),
                () -> assertEquals(COMPETITION_AGE_CATEGORY.getValue(), competition.ageCategory())
        );
    }

    private static void verifyTournament(final Tournament tournament) {
        assertTrue(Objects.nonNull(tournament));
        assertAll(
                () -> assertEquals(VALID_TOURNAMENT_KEY, tournament.getKey()),
                () -> assertEquals(TOURNAMENT_NAME, tournament.getName())
        );
    }

    private static void verifyTournament(final com.kindredgroup.kps.pricingentity.coreentity.domain.Tournament tournament) {
        assertTrue(Objects.nonNull(tournament));
        assertAll(
                () -> assertEquals(VALID_TOURNAMENT_KEY, tournament.key()),
                () -> assertEquals(TOURNAMENT_NAME, tournament.name()),
                () -> assertEquals(TOURNAMENT_STARTING_DATE, tournament.startDateTimeUtc()),
                () -> assertEquals(TOURNAMENT_ENDING_DATE, tournament.endDateTimeUtc())
        );
    }

    private static void verifyFixture(final Fixture fixture) {
        assertTrue(Objects.nonNull(fixture));
        assertAll(
                () -> assertEquals(VALID_FIXTURE_KEY, fixture.getKey()),
                () -> assertEquals(FIXTURE_NAME, fixture.getName()),
                () -> assertEquals(FIXTURE_GROUP_NAME, fixture.getGroupName()),
                () -> assertEquals(CONTEST_TYPE, fixture.getContestType()),
                () -> assertEquals(FIXTURE_ROUND_TYPE.getValue(), fixture.getRoundType().getValue()),
                () -> assertEquals(FIXTURE_STARTING_DATE, fixture.getStartsAt())
        );
    }

    private static void verifyFixture(final com.kindredgroup.kps.pricingentity.coreentity.domain.Fixture fixture) {
        assertTrue(Objects.nonNull(fixture));
        assertAll(
                () -> assertEquals(VALID_FIXTURE_KEY, fixture.key()),
                () -> assertEquals(FIXTURE_NAME, fixture.name()),
                () -> assertEquals(FIXTURE_GROUP_NAME, fixture.groupName()),
                () -> assertEquals(CONTEST_TYPE, fixture.contestType()),
                () -> assertEquals(FIXTURE_ROUND_TYPE.getValue(), fixture.roundType()),
                () -> assertEquals(FIXTURE_STARTING_DATE, fixture.startDateTimeUtc())
        );
    }

    private void verifyVenue(final com.kindredgroup.kps.pricingentity.coreentity.domain.Venue venue) {
        assertTrue(Objects.nonNull(venue));
        assertAll(
                () -> assertEquals(VALID_VENUE_KEY, venue.key()),
                () -> assertEquals(VENUE_NAME, venue.name()),
                () -> assertEquals(CONTEST_TYPE, venue.contestType()),
                () -> assertEquals(COUNTRY_CODE, venue.countryCode())
        );
    }
}