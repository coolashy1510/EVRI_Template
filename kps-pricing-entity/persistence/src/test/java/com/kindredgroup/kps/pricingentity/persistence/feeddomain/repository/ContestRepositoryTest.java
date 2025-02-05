package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContestRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    PropositionRepository propositions;

    private static Stream<Arguments> provideTypeAndStatus() {
        return Arrays.stream(ContestType.values())
                     .flatMap(type -> Arrays.stream(ContestStatus.values())
                                            .map(status -> Arguments.of(type.getValue(), status.getValue())));
    }

    @ParameterizedTest
    @MethodSource(value = {"provideTypeAndStatus"})
    void typeAndStatusConstraints_ok(String type, String status) {
        assertDoesNotThrow(() -> createContest(ContestType.of(type), ContestStatus.of(status)));
    }

    @Test
    void keyConstraint_exceptionThrown() {
        Contest existingContest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);
        String existingKey = existingContest.getKey();

        final Contest contest = new Contest();
        contest.setKey(existingKey);
        contest.setName("contest-" + existingKey.substring(existingKey.length() - 4));
        contest.setStatus(ContestStatus.IN_PLAY);
        contest.setType(ContestType.BADMINTON);

        assertThrows(DataIntegrityViolationException.class, () -> contestRepository.saveAndFlush(contest));
    }

    @Test
    void findByKey_ok() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);

        Contest result = contestRepository.findByKey(contest.getKey()).orElseThrow();
        assertEquals(contest.getName(), result.getName());
        assertEquals(contest.getStatus(), result.getStatus());
        assertEquals(contest.getType(), result.getType());
//        test replace trigger with JPA methods
        OffsetDateTime savedTimestamp = contest.getUpdatedAt();
        assertNotNull(savedTimestamp);
        assertTrue(savedTimestamp.isBefore(OffsetDateTime.now()) && savedTimestamp.isAfter(OffsetDateTime.now().minusMinutes(5)));
        Optional<Contest> persistedContest = contestRepository.findByKey(contest.getKey());
        assertTrue(persistedContest.isPresent());
        persistedContest.get().setStatus(ContestStatus.CONCLUDED);
        contestRepository.saveAndFlush(persistedContest.get());
        assertEquals(1, contestRepository.count());
        assertEquals(ContestStatus.CONCLUDED, persistedContest.get().getStatus());
        OffsetDateTime updatedTimestamp = persistedContest.get().getUpdatedAt();
        assertNotNull(updatedTimestamp);
        assertTrue(updatedTimestamp.isBefore(OffsetDateTime.now()) && updatedTimestamp.isAfter(savedTimestamp));
    }

    @Test
    void notNullNameConstraint__exceptionThrown() {
        final Contest contest = createContest(ContestType.BADMINTON, ContestStatus.IN_PLAY);
        contest.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> contestRepository.saveAndFlush(contest));
    }

    @Test
    void notNullTypeConstraint__exceptionThrown() {
        final Contest contest = createContest(ContestType.BADMINTON, ContestStatus.IN_PLAY);
        contest.setType(null);
        assertThrows(DataIntegrityViolationException.class, () -> contestRepository.saveAndFlush(contest));
    }

    @Test
    void notNullStatusConstraint__exceptionThrown() {
        final Contest contest = createContest(ContestType.BADMINTON, ContestStatus.IN_PLAY);
        contest.setStatus(null);
        assertThrows(DataIntegrityViolationException.class, () -> contestRepository.saveAndFlush(contest));
    }

}
