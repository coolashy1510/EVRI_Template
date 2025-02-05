package com.kindredgroup.kps.pricingentity.persistence.pricingdomain.service;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.*;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Fraction;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeFraction;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.OptionEntity;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Outcome;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.OutcomeRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.service.OutcomeResultServiceImpl;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.service.PropositionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OutcomeResultServiceImplTest {

    @Mock
    private PropositionServiceImpl propositionService;
    @Mock
    private OutcomeRepository outcomeRepository;
    @InjectMocks
    private OutcomeResultServiceImpl outcomeResultService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ValidOutcomeResult_SavesOutcomes() {
        OutcomeResult outcomeResult = createSampleOutcomeResult();
        Proposition proposition = createPropositionWithMatchingOptionsAndVariants();
        when(propositionService.get(outcomeResult.getContestKey(), outcomeResult.getPropositionKey())).thenReturn(Optional.of(proposition));

        outcomeResultService.save(outcomeResult);

        verify(outcomeRepository, times(1)).saveAll(anyList());
    }

    @Test
    void save_MissingProposition_ThrowsException() {
        OutcomeResult outcomeResult = createSampleOutcomeResult();
        when(propositionService.get(outcomeResult.getContestKey(), outcomeResult.getPropositionKey())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> outcomeResultService.save(outcomeResult));
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void getOutcomeResult() {
        Proposition proposition = createPropositionWithMatchingOptionsAndVariants();
        Outcome outcome = new Outcome(new Option("team_1", proposition, OptionType.T1),
                new Variant("plain", proposition, VariantType.PLAIN),
                0,1,0,1);
        when(outcomeRepository.findByContestKey("testContestKey")).thenReturn(List.of(outcome));
        List<OutcomeResult> outcomeResultList = outcomeResultService.getOutcomeResult("testContestKey");
        assertFalse(outcomeResultList.isEmpty());
        assertEquals(outcome.getProposition().getKey(), outcomeResultList.get(0).getPropositionKey());
        assertEquals(outcome.getOption().getKey(), outcomeResultList.get(0).getOutcomeFractions().get(0).optionKey());
        assertEquals(outcome.getVariant().getKey(), outcomeResultList.get(0).getOutcomeFractions().get(0).variantKey());
        assertEquals(outcome.getRefundDenominator(), outcomeResultList.get(0).getOutcomeFractions().get(0).refundFraction().denominator());
    }

    @Test
    void getOutcomeResult_WithEntityKeys() {
        Proposition proposition = createPropositionWithMatchingOptionsAndVariants();
        var team1Option = new Option("team_1", proposition, OptionType.PARTICIPANT);
        team1Option.setOptionEntities((List.of(new OptionEntity(team1Option, "entityKey1", "type"))));
        Outcome outcome = new Outcome(
                team1Option,
                new Variant("plain", proposition, VariantType.PLAIN),
                0, 1, 0, 1);
        when(outcomeRepository.findByContestKey("testContestKey")).thenReturn(List.of(outcome));
        List<OutcomeResult> outcomeResultList = outcomeResultService.getOutcomeResult("testContestKey");
        assertFalse(outcomeResultList.isEmpty());
        assertEquals(outcome.getProposition().getKey(), outcomeResultList.get(0).getPropositionKey());
        assertEquals(outcome.getOption().getKey(), outcomeResultList.get(0).getOutcomeFractions().get(0).optionKey());
        assertEquals(outcome.getVariant().getKey(), outcomeResultList.get(0).getOutcomeFractions().get(0).variantKey());
        assertEquals(outcome.getOption().getOptionEntities().get(0).getKey(),
                outcomeResultList.get(0).getOutcomeFractions().get(0).quantContractType()
                                 .getField(QuantFieldName.PLAYER_KEY, String.class)
                                 .orElseThrow(() -> new IllegalStateException("Player key not found")));
        assertEquals(outcome.getRefundDenominator(),
                outcomeResultList.get(0).getOutcomeFractions().get(0).refundFraction().denominator());
    }

    @Test
    void getOutcomeResult_ThrowsException() {
        when(outcomeRepository.findByContestKey("testContestKey")).thenReturn(List.of());
        assertThrows(IllegalStateException.class, () -> outcomeResultService.getOutcomeResult("testContestKey"));
    }


    private OutcomeResult createSampleOutcomeResult() {
        List<OutcomeFraction> outcomeFractions = getOutcomeFractions();

        OutcomeResult outcomeResult = new OutcomeResult("ca741f443e9cfc6832dd7e391a68008b",
                false, "2nd_half_buriram_united_clean_sheet", "BetRadar", FeedProvider.BET_RADAR, OffsetDateTime.parse("2023-11-24T13:41:39.015Z"));
        outcomeResult.getOutcomeFractions().addAll(outcomeFractions);

        return outcomeResult;
    }

    private static List<OutcomeFraction> getOutcomeFractions() {
        List<OutcomeFraction> outcomeFractions = new ArrayList<>();
        outcomeFractions.add(new OutcomeFraction("yes", new QuantMarketType(QuantMarketTypeClassName.ANY_TEAM_TO_WIN),"plain", new QuantContractType(QuantContractTypeClassName.YES), new Fraction(0, 1), new Fraction(1, 1)));
        outcomeFractions.add(new OutcomeFraction("no", new QuantMarketType(QuantMarketTypeClassName.ANY_TEAM_TO_WIN), "plain", new QuantContractType(QuantContractTypeClassName.NO), new Fraction(0, 1), new Fraction(0, 1)));
        return outcomeFractions;
    }

    private Proposition createPropositionWithMatchingOptionsAndVariants() {
        Proposition proposition = new Proposition();
        List<Option> options = new ArrayList<>();
        options.add(new Option("team_1", proposition, OptionType.T1));
        options.add(new Option("team_2", proposition, OptionType.T2));
        options.add(new Option("draw", proposition, OptionType.DRAW));

        List<Variant> variants = new ArrayList<>();
        variants.add(new Variant("plain", proposition, VariantType.LINE));

        proposition.setOptions(options);
        proposition.setVariants(variants);
        proposition.setKey("1x2");
        proposition.setType("1x2");
        return proposition;
    }
}

