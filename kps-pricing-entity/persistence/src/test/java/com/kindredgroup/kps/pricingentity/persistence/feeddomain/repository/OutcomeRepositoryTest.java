package com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OutcomeRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    VariantRepository variantRepository;

    @Autowired
    OptionRepository optionRepository;

    @Autowired
    OutcomeRepository outcomeRepository;
    @Autowired
    PropositionRepository propositionRepository;
    private Proposition proposition;
    private Variant marginVariant;
    private Option option;
    private Variant plainVariant;
    private Variant overUnderVariant;

    @BeforeEach
    void setUp() {
        Contest contest = createContest(ContestType.FOOTBALL, ContestStatus.PRE_GAME);
        proposition = createProposition(contest);
        Proposition wrongProposition = createProposition(contest);

        String variantKey = "margin";
        marginVariant = new Variant(variantKey, proposition, VariantType.MARGIN);
        marginVariant.setName("Margin");
        marginVariant = variantRepository.save(marginVariant);

        plainVariant = new Variant("p", proposition, VariantType.PLAIN);
        plainVariant.setName("P");
        plainVariant = variantRepository.save(plainVariant);

        overUnderVariant = new Variant("1", wrongProposition, VariantType.OVER_UNDER);
        overUnderVariant.setName("OU");
        overUnderVariant = variantRepository.save(overUnderVariant);

        final String optionKey = "key1";
        option = new Option(optionKey, proposition, OptionType.DRAW);
        option.setName("Name");

        option = optionRepository.save(option);
    }

    @Test
    void outcome_ok() {
        assertTrue(outcomeRepository.findAll().isEmpty());
        Outcome outcomePlain = new Outcome(option, plainVariant, 0,1,0,1);
        outcomeRepository.save(outcomePlain);
        Outcome outcomeMargin = new Outcome(option, marginVariant, 1,0,0,0);
        outcomeRepository.save(outcomeMargin);

        assertEquals(2, outcomeRepository.findAll().size());

        final List<Outcome> outcomeList = outcomeRepository.findByProposition(proposition);
        assertTrue(outcomeList.contains(outcomePlain));
        assertTrue(outcomeList.contains(outcomeMargin));

        final Optional<Outcome> persistedPlainOutcome = outcomeRepository.findByKey(proposition.getKey(), option.getKey(), plainVariant.getKey());
        assertTrue(persistedPlainOutcome.isPresent());
        assertEquals(outcomePlain, persistedPlainOutcome.get());

        final Optional<Outcome> persistedMarginOutcome = outcomeRepository.findByKey(proposition.getKey(), option.getKey(), marginVariant.getKey());
        assertTrue(persistedMarginOutcome.isPresent());
        assertEquals(outcomeMargin, persistedMarginOutcome.get());
    }

    @Test
    void outcome_different_proposition() {
        assertTrue(outcomeRepository.findAll().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> new Outcome(option, overUnderVariant, 0,1,0,1));
    }

}
