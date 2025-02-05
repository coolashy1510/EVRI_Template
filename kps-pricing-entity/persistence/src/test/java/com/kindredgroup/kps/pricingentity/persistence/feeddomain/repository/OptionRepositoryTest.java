package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.OptionEntity;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OptionRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    OptionRepository optionRepository;
    @Autowired
    PropositionRepository propositionRepository;

    private static Stream<OptionType> provideType() {
        return Arrays.stream(OptionType.values());
    }

    @Test
    void option_ok() {
        assertTrue(optionRepository.findAll().isEmpty());

        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);

        Proposition proposition = createProposition(contest);

        final String optionKey = "rk_pelister_over_55_5";
        Option option = new Option(optionKey, proposition, OptionType.UNSPECIFIED);
        option.setName("RK Pelister & over 55.5");
        List<OptionEntity> optionEntities = new ArrayList<>();
        optionEntities.add(
                new OptionEntity(option, "entityKey1", "type"));
        optionEntities.add(
                new OptionEntity(option, "entityKey2", "type")
        );
        option.setOptionEntities(optionEntities);

        final Option saved = optionRepository.save(option);

        //  test replace trigger with JPA methods
        OffsetDateTime savedTimestamp = saved.getUpdatedAt();
        assertNotNull(savedTimestamp);
        assertTrue(savedTimestamp.isBefore(OffsetDateTime.now()) && savedTimestamp.isAfter(OffsetDateTime.now().minusMinutes(5)));
        optionRepository.flush();
        Optional<Option> persistedOption = optionRepository.findById(option.getId());
        assertTrue(persistedOption.isPresent());
        persistedOption.get().setName("Draw");
        assertEquals(2, persistedOption.get().getOptionEntities().size());
        optionRepository.saveAndFlush(persistedOption.get());
        assertEquals(1, optionRepository.findAll().size());
        assertEquals("Draw", persistedOption.get().getName());
        OffsetDateTime updatedTimestamp = persistedOption.get().getUpdatedAt();
        assertNotNull(updatedTimestamp);
        assertTrue(updatedTimestamp.isBefore(OffsetDateTime.now()) && updatedTimestamp.isAfter(savedTimestamp));

        final Option result = optionRepository.findByPropositionAndKey(proposition, saved.getKey()).orElseThrow();
        assertEquals(proposition.getId(), result.getProposition().getId());
        assertEquals(optionKey, result.getKey());
        assertEquals(OptionType.UNSPECIFIED, result.getType());
        assertEquals(option.getName(), result.getName());
        assertTrue(option.getOptionEntities().contains(optionEntities.get(0)));
        assertTrue(option.getOptionEntities().contains(optionEntities.get(1)));
        assertNotNull(result.getProposition());
        assertEquals(proposition.getKey(), result.getProposition().getKey());

    }

    @ParameterizedTest
    @MethodSource(value = {"provideType"})
    void typeConstraint_ok(OptionType type) {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);

        Proposition proposition = createProposition(contest);

        final String optionKey = "rk_pelister_over_55_5";
        Option option = new Option(optionKey, proposition, type);
        option.setName("RK Pelister & over 55.5");

        Assertions.assertDoesNotThrow(() -> optionRepository.save(option));
    }

    @Test
    void uniqueConstraint_exceptionThrown() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);

        Proposition proposition = createProposition(contest);

        final String existingKey = "key1";
        Option option = new Option(existingKey, proposition, OptionType.DRAW);
        option.setName("Name");

        optionRepository.save(option);

        option = new Option("key2", proposition, OptionType.DRAW);
        option.setName("Name");

        optionRepository.save(option);

        final Option failingOption = new Option(existingKey, proposition, OptionType.ODD);
        failingOption.setName("another name");

        assertThrows(DataIntegrityViolationException.class, () -> optionRepository.save(failingOption));

    }

    @Test
    void notNullKeyConstraint_exceptionThrown() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);
        Proposition proposition = createProposition(contest);
        final Option option = new Option(null, proposition, OptionType.ODD);
        assertThrows(DataIntegrityViolationException.class, () -> optionRepository.save(option));
    }

    @Test
    void notNullNameConstraint_exceptionThrown() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);
        Proposition proposition = createProposition(contest);
        final Option option = new Option("key4", proposition, OptionType.ODD);
        assertThrows(DataIntegrityViolationException.class, () -> optionRepository.save(option));

    }
}
