package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;


import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Price;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropositionRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    PropositionRepository propositions;

    @Autowired
    OptionRepository optionRepository;

    @Autowired
    VariantRepository variantRepository;

    @Autowired
    PriceRepository priceRepository;

    @Test
    void proposition_ok() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);
        Proposition proposition = createProposition(contest);
        final String propositionKey1 = proposition.getKey();


        Proposition result = propositions.findByContestAndKey(contest, propositionKey1).orElseThrow();
        assertEquals(proposition.getType(), result.getType());
        assertNotNull(proposition.getContest());
        assertEquals(contest.getKey(), result.getContest().getKey());

        proposition = createProposition(contest);
        final String propositionKey2 = proposition.getKey();

        List<Proposition> items = propositions.findPropositionsByContest(contest);
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item -> item.getKey().equals(propositionKey1)));
        assertTrue(items.stream().anyMatch(item -> item.getKey().equals(propositionKey2)));

    }

    @Test
    void proposition_orphanRemoval_ok() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);
        Proposition proposition = createProposition(contest);

        Option option = new Option("option_key", proposition, OptionType.T1);
        option.setName("option_name");
        proposition.setOptions(List.of(option));
        Variant variant = new Variant("variant_key", proposition, VariantType.LINE);
        variant.setName("variant_name");
        proposition.setVariants(List.of(variant));
        Price price = new Price(option, variant, BigDecimal.TEN);
        proposition.setPrices(List.of(price));
        proposition = propositionRepository.save(proposition);

        assertEquals(1, proposition.getOptions().size());
        assertEquals(1, proposition.getVariants().size());
        assertEquals(1, proposition.getPrices().size());
        Option expectedOption = optionRepository.findByPropositionAndKey(proposition, "option_key").orElseThrow();
        Variant expectedVariant = variantRepository.findByPropositionAndKey(proposition, "variant_key").orElseThrow();
        final List<Price> expectedPrices = priceRepository.findByProposition(proposition);
        assertEquals(1, expectedPrices.size());

        assertEquals(expectedOption, proposition.getOptions().get(0));
        assertEquals(OptionType.T1, expectedOption.getType());
        assertEquals(expectedVariant, proposition.getVariants().get(0));
        assertEquals(VariantType.LINE, expectedVariant.getType());
        assertEquals(expectedPrices.get(0), proposition.getPrices().get(0));
        assertEquals(expectedOption, expectedPrices.get(0).getOption());
        assertEquals(expectedVariant, expectedPrices.get(0).getVariant());

//        test replace trigger with JPA methods
        OffsetDateTime savedTimestamp = proposition.getUpdatedAt();
        assertNotNull(savedTimestamp);
        assertTrue(savedTimestamp.isBefore(OffsetDateTime.now()) && savedTimestamp.isAfter(OffsetDateTime.now().minusMinutes(5)));
        propositionRepository.flush();
        Optional<Proposition> persistedProposition = propositionRepository.findById(proposition.getId());
        assertTrue(persistedProposition.isPresent());
        persistedProposition.get().setName("Match");
        propositionRepository.saveAndFlush(persistedProposition.get());
        assertEquals(1, propositionRepository.findAll().size());
        assertEquals("Match", persistedProposition.get().getName());
        OffsetDateTime updatedTimestamp = persistedProposition.get().getUpdatedAt();
        assertNotNull(updatedTimestamp);
        assertTrue(updatedTimestamp.isBefore(OffsetDateTime.now()) && updatedTimestamp.isAfter(savedTimestamp));

        propositionRepository.delete(proposition);

        assertTrue(optionRepository.findAll().isEmpty());
        assertTrue(variantRepository.findAll().isEmpty());
        assertTrue(priceRepository.findAll().isEmpty());

    }

    @Test
    void keyConstraint_exceptionThrown() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);
        Proposition existingProposition = createProposition(contest);

        Proposition proposition1 = new Proposition();
        proposition1.setKey(existingProposition.getKey());
        proposition1.setContest(contest);
        proposition1.setName("Football simple");
        proposition1.setType("total");

        assertThrows(DataIntegrityViolationException.class, () -> propositionRepository.save(proposition1));

        Contest anotherContest = createContest(ContestType.BANDY, ContestStatus.PRE_GAME);

        Proposition proposition2 = new Proposition();
        proposition2.setKey(existingProposition.getKey());
        proposition2.setContest(anotherContest);
        proposition2.setName("Football simple");
        proposition2.setType("total");

        propositionRepository.save(proposition2);
    }

    @Test
    void notNullNameConstraint__exceptionThrown() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);
        final Proposition proposition = createProposition(contest);
        proposition.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> propositionRepository.saveAndFlush(proposition));
    }

    @Test
    void notNullTypeConstraint__exceptionThrown() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);
        final Proposition proposition = createProposition(contest);
        proposition.setType(null);
        assertThrows(DataIntegrityViolationException.class, () -> propositionRepository.saveAndFlush(proposition));
    }

    @Test
    void notNullContestConstraint__exceptionThrown() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);
        final Proposition proposition = createProposition(contest);
        proposition.setContest(null);
        assertThrows(DataIntegrityViolationException.class, () -> propositionRepository.saveAndFlush(proposition));
    }

}
