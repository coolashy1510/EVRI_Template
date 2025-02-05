package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;

import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
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

class VariantRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    VariantRepository variantRepository;
    @Autowired
    PropositionRepository propositionRepository;

    private static Stream<VariantType> provideType() {
        return Arrays.stream(VariantType.values());
    }

    @Test
    void variant_ok() {
        assertTrue(variantRepository.findAll().isEmpty());

        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);

        Proposition proposition = createProposition(contest);

        final String variantKey = "line";
        Variant variant = new Variant(variantKey, proposition, VariantType.LINE);
        variant.setName("Line");

        final Variant saved = variantRepository.save(variant);

        final Variant result = variantRepository.findByPropositionAndKey(proposition, saved.getKey()).orElseThrow();
        assertEquals(proposition.getId(), result.getProposition().getId());
        assertEquals(variantKey, result.getKey());
        assertEquals(VariantType.LINE, result.getType());
        assertEquals(variant.getName(), result.getName());
        assertNotNull(result.getProposition());
        assertEquals(proposition.getKey(), result.getProposition().getKey());

//        test replace trigger with JPA methods
        OffsetDateTime savedTimestamp = saved.getUpdatedAt();
        assertNotNull(savedTimestamp);
        assertTrue(savedTimestamp.isBefore(OffsetDateTime.now()) && savedTimestamp.isAfter(OffsetDateTime.now().minusMinutes(5)));
        variantRepository.flush();
        Optional<Variant> persistedVariant = variantRepository.findById(saved.getId());
        assertTrue(persistedVariant.isPresent());
        persistedVariant.get().setName("Plain");
        variantRepository.saveAndFlush(persistedVariant.get());
        assertEquals(1, variantRepository.findAll().size());
        assertEquals("Plain", persistedVariant.get().getName());
        OffsetDateTime updatedTimestamp = persistedVariant.get().getUpdatedAt();
        assertNotNull(updatedTimestamp);
        assertTrue(updatedTimestamp.isBefore(OffsetDateTime.now()) && updatedTimestamp.isAfter(savedTimestamp));
    }

    @ParameterizedTest
    @MethodSource(value = {"provideType"})
    void typeConstraint_ok(VariantType type) {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);

        Proposition proposition = createProposition(contest);

        final String variantKey = "0.5";
        Variant variant = new Variant(variantKey, proposition, type);
        variant.setName("Line");

        Assertions.assertDoesNotThrow(() -> variantRepository.save(variant));
    }

    @Test
    void uniqueConstraint__exceptionThrown() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);

        Proposition proposition = createProposition(contest);

        final String existingKey = "key1";
        Variant variant = new Variant(existingKey, proposition, VariantType.LINE);
        variant.setName("Name");

        variantRepository.save(variant);

        variant = new Variant("key2", proposition, VariantType.LINE);
        variant.setName("Name");

        variantRepository.save(variant);

        final Variant failingOption = new Variant(existingKey, proposition, VariantType.MARGIN);
        variant.setName("another name");

        assertThrows(DataIntegrityViolationException.class, () -> variantRepository.save(failingOption));

    }

    @Test
    void notNullConstraint__exceptionThrown() {

        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);

        Proposition proposition = createProposition(contest);

        final Variant variant3 = new Variant(null, proposition, VariantType.LINE);
        variant3.setName("another name");

        assertThrows(DataIntegrityViolationException.class, () -> variantRepository.save(variant3));

        final Variant variant4 = new Variant("key4", proposition, VariantType.LINE);
        assertThrows(DataIntegrityViolationException.class, () -> variantRepository.save(variant4));


    }


}
