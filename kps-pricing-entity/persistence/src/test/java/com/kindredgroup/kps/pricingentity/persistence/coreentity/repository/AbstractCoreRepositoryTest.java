package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionAgeCategory;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionType;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import com.kindredgroup.kps.pricingentity.coreentity.domain.FixtureRoundType;
import com.kindredgroup.kps.pricingentity.persistence.config.RepositoryConfig;
import com.kindredgroup.kps.pricingentity.persistence.config.TestDataSourceConfig;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Competition;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Fixture;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Participant;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Team;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Tournament;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Venue;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(classes = {RepositoryConfig.class, TestDataSourceConfig.class})
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = {"/create_core_schema.sql"})
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = {"/drop_core_schema.sql"})
@Transactional
public abstract class AbstractCoreRepositoryTest {

    @Autowired
    protected CompetitionRepository competitionRepository;
    @Autowired
    protected TournamentRepository tournamentRepository;
    @Autowired
    protected FixtureRepository fixtureRepository;
    @Autowired
    protected VenueRepository venueRepository;
    @Autowired
    protected TeamRepository teamRepository;
    @Autowired
    protected ParticipantRepository participantRepository;


    protected Competition createCompetition(CompetitionType type, CompetitionAgeCategory ageCategory, CoreEntityGender gender) {
        Competition competition = new Competition();
        final String uuid = UUID.randomUUID().toString();
        competition.setKey(uuid);
        competition.setType(type);
        competition.setName("competition-" + uuid.substring(uuid.length() - 4));
        competition.setContestType("Football");
        competition.setAgeCategory(ageCategory);
        competition.setGender(gender);
        return competitionRepository.save(competition);
    }

    protected Tournament createTournament(Competition competition) {
        Tournament tournament = new Tournament();
        tournament.setKey(UUID.randomUUID().toString());
        tournament.setName("WorldCup 2022");
        tournament.setStartsAt(OffsetDateTime.now());
        tournament.setEndsAt(OffsetDateTime.now().plus(7L, ChronoUnit.DAYS));
        tournament.setCompetition(competition);
        return tournamentRepository.save(tournament);
    }

    protected Fixture createFixture(FixtureRoundType roundType, Tournament tournament, Venue venue) {
        Fixture fixture = new Fixture();
        fixture.setKey(UUID.randomUUID().toString());
        fixture.setGroupName("2007 WTA Antwerp");
        fixture.setName("Daniil, Eleni vs CHAKVETADZE A.");
        fixture.setRoundType(roundType);
        fixture.setContestType("Football");
        fixture.setStartsAt(OffsetDateTime.now());
        fixture.setTournament(tournament);
        fixture.setVenue(venue);
        return fixtureRepository.save(fixture);
    }

    protected Venue createVenue() {
        Venue venue = new Venue();
        venue.setKey(UUID.randomUUID().toString());
        venue.setContestType("Football");
        venue.setName("Centre Court");
        venue.setCountryCode("FRA");
        return venueRepository.save(venue);
    }

    protected Team createTeam(Venue venue) {
        Team team = new Team();
        team.setContestType("Football");
        team.setGender(CoreEntityGender.NOT_KNOWN);
        team.setHomeVenue(venue);
        team.setName("Manchester City");
        team.setKey(UUID.randomUUID().toString());
        return teamRepository.save(team);
    }

    protected Participant createParticipant() {
        Participant participant = new Participant();
        participant.setContestType("Football");
        participant.setKey(UUID.randomUUID().toString());
        participant.setName("Erling Haaland");
        participant.setGender(CoreEntityGender.MALE);
        participant.setDateOfBirth(OffsetDateTime.now());
        return participantRepository.save(participant);
    }


}
