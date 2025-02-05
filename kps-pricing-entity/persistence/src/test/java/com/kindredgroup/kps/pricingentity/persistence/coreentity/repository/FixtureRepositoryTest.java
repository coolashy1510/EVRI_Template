package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionAgeCategory;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionType;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import com.kindredgroup.kps.pricingentity.coreentity.domain.FixtureRoundType;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Competition;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Fixture;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Tournament;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Venue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FixtureRepositoryTest extends AbstractCoreRepositoryTest {


    private static Stream<Arguments> provideRoundType() {
        return Arrays.stream(FixtureRoundType.values())
                .map(roundType -> Arguments.of(roundType.getValue()));
    }

    @ParameterizedTest
    @MethodSource(value = {"provideRoundType"})
    void competitionConstraints_ok(String type) {
        Competition competition = createCompetition(CompetitionType.NOT_APPLICABLE, CompetitionAgeCategory.U18,
                CoreEntityGender.MIXED);
        Tournament tournament = createTournament(competition);
        Venue venue = createVenue();
        assertDoesNotThrow(() -> createFixture(FixtureRoundType.of(type), tournament, venue));
    }

    @Test
    void keyConstraint_exceptionThrown() {
        Fixture existingFixture = createFixture();
        String existingKey = existingFixture.getKey();

        final Fixture fixture = new Fixture();
        fixture.setKey(existingKey);
        fixture.setGroupName("2012 WTA Antwerp");
        fixture.setName("Medvedev - Tsitsipas");
        fixture.setRoundType(FixtureRoundType.ROUND_OF_32);
        fixture.setContestType("Tennis");
        fixture.setStartsAt(OffsetDateTime.now());

        assertThrows(DataIntegrityViolationException.class, () -> fixtureRepository.save(fixture));
    }

    @Test
    void findByKey_ok() {
        assertTrue(fixtureRepository.findAll().isEmpty());
        Fixture existingFixture = createFixture();

        Fixture result = fixtureRepository.findByKey(existingFixture.getKey()).orElseThrow();
        assertEquals(existingFixture.getName(), result.getName());
        assertEquals(existingFixture.getGroupName(), result.getGroupName());
        assertEquals(existingFixture.getRoundType(), result.getRoundType());
        assertEquals(existingFixture.getContestType(), result.getContestType());
        assertEquals(existingFixture.getVenue(), result.getVenue());
        assertEquals(existingFixture.getTournament(), result.getTournament());
    }

    @Test
    void findByTournament_ok() {
        assertTrue(fixtureRepository.findAll().isEmpty());
        Fixture existingFixture = createFixture();
        Fixture result = fixtureRepository.findByTournament(existingFixture.getTournament()).get(0);
        assertEquals(existingFixture.getName(), result.getName());
        assertEquals(existingFixture.getGroupName(), result.getGroupName());
        assertEquals(existingFixture.getRoundType(), result.getRoundType());
        assertEquals(existingFixture.getContestType(), result.getContestType());
        assertEquals(existingFixture.getVenue(), result.getVenue());
        assertEquals(existingFixture.getTournament(), result.getTournament());
    }

    @Test
    void notNullNameConstraint__exceptionThrown() {
        final Fixture fixture = createFixture();
        fixture.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> fixtureRepository.saveAndFlush(fixture));
    }

    @Test
    void notNullContestTypeConstraint__exceptionThrown() {
        final Fixture fixture = createFixture();
        fixture.setContestType(null);
        assertThrows(DataIntegrityViolationException.class, () -> fixtureRepository.saveAndFlush(fixture));
    }

    @Test
    void notNullStartsAtConstraint__exceptionThrown() {
        final Fixture fixture = createFixture();
        fixture.setStartsAt(null);
        assertThrows(DataIntegrityViolationException.class, () -> fixtureRepository.saveAndFlush(fixture));
    }

    @Test
    void nullConstraint__noExceptionThrown() {
        final Fixture fixture = createFixture();
        fixture.setGroupName(null);
        fixture.setRoundType(null);
        fixture.setTournament(null);
        fixture.setVenue(null);
        Assertions.assertDoesNotThrow(() -> fixtureRepository.saveAndFlush(fixture));
    }

    private Fixture createFixture() {
        Competition competition = createCompetition(CompetitionType.NOT_APPLICABLE, CompetitionAgeCategory.U18,
                CoreEntityGender.MIXED);
        Tournament tournament = createTournament(competition);
        Venue venue = createVenue();
        return createFixture(FixtureRoundType.PRELIMINARY_FIRST_ROUND, tournament, venue);
    }

}
