package com.kindredgroup.kps.pricingentity.persistence.coreentity.repository;

import java.util.Arrays;
import java.util.stream.Stream;

import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionAgeCategory;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CompetitionType;
import com.kindredgroup.kps.pricingentity.coreentity.domain.CoreEntityGender;
import com.kindredgroup.kps.pricingentity.persistence.coreentity.model.Competition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompetitionRepositoryTest extends AbstractCoreRepositoryTest {


    private static Stream<Arguments> provideTypeCategoryAndGender() {
        return Arrays.stream(CompetitionType.values())
                     .flatMap(type -> Arrays.stream(CompetitionAgeCategory.values())
                                            .flatMap(ageCategory -> Arrays.stream(CoreEntityGender.values())
                                                                          .map(gender -> Arguments.of(type.getValue(),
                                                                                  ageCategory.getValue(), gender.getValue()))));
    }

    @ParameterizedTest
    @MethodSource(value = {"provideTypeCategoryAndGender"})
    void competitionConstraints_ok(String type, String category, String gender) {
        assertDoesNotThrow(() -> createCompetition(CompetitionType.of(type), CompetitionAgeCategory.of(category),
                CoreEntityGender.of(gender)));
    }

    @Test
    void keyConstraint_exceptionThrown() {
        Competition existingContest = createCompetition(CompetitionType.CUP, CompetitionAgeCategory.Y10, CoreEntityGender.MALE);
        String existingKey = existingContest.getKey();

        final Competition competition = new Competition();
        competition.setKey(existingKey);
        competition.setType(CompetitionType.LEAGUE);
        competition.setName("competition-TEST");
        competition.setContestType("Football");
        competition.setAgeCategory(CompetitionAgeCategory.U11);
        competition.setGender(CoreEntityGender.MALE);

        assertThrows(DataIntegrityViolationException.class, () -> competitionRepository.save(competition));

    }

    @Test
    void findByKey_ok() {
        assertTrue(competitionRepository.findAll().isEmpty());

        Competition competition = createCompetition(CompetitionType.CUP, CompetitionAgeCategory.Y10, CoreEntityGender.MALE);

        Competition result = competitionRepository.findByKey(competition.getKey()).orElseThrow();
        assertEquals(competition.getName(), result.getName());
        assertEquals(competition.getAgeCategory(), result.getAgeCategory());
        assertEquals(competition.getType(), result.getType());
        assertEquals(competition.getContestType(), result.getContestType());
        assertEquals(competition.getGender(), result.getGender());
    }

    @Test
    void notNullNameConstraint__exceptionThrown() {

        final Competition competition = createCompetition(CompetitionType.CUP, CompetitionAgeCategory.Y10, CoreEntityGender.MALE);
        competition.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> competitionRepository.saveAndFlush(competition));

    }

    @Test
    void notNullTypeConstraint__exceptionThrown() {
        final Competition competition = createCompetition(CompetitionType.CUP, CompetitionAgeCategory.Y10, CoreEntityGender.MALE);
        competition.setType(null);
        assertThrows(DataIntegrityViolationException.class, () -> competitionRepository.saveAndFlush(competition));
    }

    @Test
    void notNullAgeCategoryConstraint__exceptionThrown() {
        final Competition competition = createCompetition(CompetitionType.CUP, CompetitionAgeCategory.Y10, CoreEntityGender.MALE);
        competition.setAgeCategory(null);
        assertThrows(DataIntegrityViolationException.class, () -> competitionRepository.saveAndFlush(competition));
    }

    @Test
    void notNullGenderConstraint__exceptionThrown() {
        final Competition competition = createCompetition(CompetitionType.CUP, CompetitionAgeCategory.Y10, CoreEntityGender.MALE);
        competition.setGender(null);
        assertThrows(DataIntegrityViolationException.class, () -> competitionRepository.saveAndFlush(competition));
    }

    @Test
    void notNullKeyConstraint__exceptionThrown() {
        Competition competition = new Competition();
        competition.setType(CompetitionType.CUP);
        competition.setName("competition");
        competition.setContestType("Football");
        competition.setAgeCategory(CompetitionAgeCategory.Y10);
        competition.setGender(CoreEntityGender.MALE);
        competition.setKey(null);
        assertThrows(DataIntegrityViolationException.class, () -> competitionRepository.saveAndFlush(competition));
    }

}
