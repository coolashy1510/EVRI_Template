package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.pricingdomain.Argument;
import com.kindredgroup.kps.internal.api.pricingdomain.Entity;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Option;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.OptionV2;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.OptionRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.PropositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @Mock
    private ContestServiceImpl contestService;
    private OptionServiceImpl optionService;

    @Mock
    private OptionRepository optionRepository;
    @Mock
    private PropositionRepository propositionRepository;

    @BeforeEach
    void setUp() {
        optionService = new OptionServiceImpl(optionRepository, contestService, propositionRepository);
    }

    @Test
    void convert_ok() {
        Proposition proposition = mock(Proposition.class);
        List<Option> options = new ArrayList<>();
        options.add(Option.builder().optionKey("key1").optionType(OptionType.T1).name("name1").build());
        options.add(Option.builder().optionKey("key2").optionType(OptionType.T2).name("name2").build());

        final List<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option> result = OptionServiceImpl.convert(
                options,
                proposition);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(
                option -> option.getName().equals("name1") && option.getProposition().equals(proposition) &&
                        option.getKey().equals("key1") && option.getType().equals(OptionType.T1)));
        assertTrue(result.stream().anyMatch(
                option -> option.getName().equals("name2") && option.getProposition().equals(proposition) &&
                        option.getKey().equals("key2") && option.getType().equals(OptionType.T2)));
    }

    @Test
    void convert_entity_keys_ok() {
        Proposition proposition = mock(Proposition.class);
        List<Option> options = new ArrayList<>();
        options.add(Option.builder().optionKey("key1").optionType(OptionType.T1).name("name1")
                          .entities(List.of(new Entity("50Q60OCU19YJMCHELQBC1OFBC", "team_key"))).build());
        options.add(Option.builder().optionKey("key2").optionType(OptionType.T2).name("name2")
                          .entities(List.of(new Entity("50Q60OCU19YJMCHELQBC1OFBD", "team_key"))).build());

        final List<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option> result = OptionServiceImpl.convert(
                options,
                proposition);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(
                option -> option.getName().equals("name1") && option.getProposition().equals(proposition) &&
                        option.getKey().equals("key1") && option.getType().equals(OptionType.T1) &&
                        option.getOptionEntities().getFirst().getKey().equals("50Q60OCU19YJMCHELQBC1OFBC")));
        assertTrue(result.stream().anyMatch(
                option -> option.getName().equals("name2") && option.getProposition().equals(proposition) &&
                        option.getKey().equals("key2") && option.getType().equals(OptionType.T2) &&
                        option.getOptionEntities().getFirst().getKey().equals("50Q60OCU19YJMCHELQBC1OFBD")));
    }

    @Test
    void convert_emptyList_ok() {
        Proposition proposition = mock(Proposition.class);
        final List<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option> result = OptionServiceImpl.convert(
                List.of(), proposition);
        assertTrue(result.isEmpty());
    }

    @Test
    void convert_v2_ok() {
        Proposition proposition = mock(Proposition.class);
        List<OptionV2> options = new ArrayList<>();
        options.add(OptionV2.builder().optionKey("key1").optionType(OptionType.V2_T1).name("name1").build());
        options.add(OptionV2.builder().optionKey("key2").optionType(OptionType.V2_T2).name("name2").build());

        final List<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option> result = OptionServiceImpl.convertV2(
                options,
                proposition);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(
                option -> option.getName().equals("name1") && option.getProposition().equals(proposition) &&
                        option.getKey().equals("key1") && option.getType().equals(OptionType.V2_T1)));
        assertTrue(result.stream().anyMatch(
                option -> option.getName().equals("name2") && option.getProposition().equals(proposition) &&
                        option.getKey().equals("key2") && option.getType().equals(OptionType.V2_T2)));
    }

    @Test
    void convert_withArguments_ok() {
        Proposition proposition = mock(Proposition.class);
        List<OptionV2> options = new ArrayList<>();
        options.add(OptionV2.builder().optionKey("key1").optionType(OptionType.V2_T1).name("name1")
                            .arguments(List.of(new Argument("a1", "v1", new Entity("50Q60OCU19YJMCHELQBC1OFBC", "entity type"))))
                            .build());
        options.add(OptionV2.builder().optionKey("key2").optionType(OptionType.V2_T2).name("name2")
                            .arguments(List.of(new Argument("a2", "v2", new Entity("50Q60OCU19YJMCHELQBC1OFBD", "entity type"))))
                            .build());

        final List<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option> result = OptionServiceImpl.convertV2(
                options,
                proposition);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(
                option -> option.getName().equals("name1") && option.getProposition().equals(proposition) &&
                        option.getKey().equals("key1") && option.getType().equals(OptionType.V2_T1) &&
                        option.getOptionEntities().getFirst().getKey().equals("50Q60OCU19YJMCHELQBC1OFBC")));
        assertTrue(result.stream().anyMatch(
                option -> option.getName().equals("name2") && option.getProposition().equals(proposition) &&
                        option.getKey().equals("key2") && option.getType().equals(OptionType.V2_T2) &&
                        option.getOptionEntities().getFirst().getKey().equals("50Q60OCU19YJMCHELQBC1OFBD")));
    }

    @Test
    void convert_emptyListV2_ok() {
        Proposition proposition = mock(Proposition.class);
        final List<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option> result = OptionServiceImpl.convertV2(
                List.of(), proposition);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_contestAbsent_exceptionThrown() {
        final OptionChanged optionChanged = mock(OptionChanged.class);
        when(optionChanged.contestKey()).thenReturn("key");
        when(contestService.findByKey(any())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> optionService.save(optionChanged));
        verify(contestService, times(1)).findByKey("key");
        verifyNoInteractions(optionRepository);

    }

    @Test
    void save_propositionAbsent_exceptionThrown() {
        final OptionChanged optionChanged = mock(OptionChanged.class);
        when(optionChanged.contestKey()).thenReturn("12245b7ae4b58d21f6e395229b43f1e7");
        when(optionChanged.propositionKey()).thenReturn("propositionKey");
        final Contest contest = mock(Contest.class);
        when(contestService.findByKey(any())).thenReturn(Optional.of(contest));
        when(propositionRepository.findByContestAndKey(eq(contest), any())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> optionService.save(optionChanged));
        verify(contestService, times(1)).findByKey("12245b7ae4b58d21f6e395229b43f1e7");
        verifyNoMoreInteractions(contestService);
        verify(propositionRepository, times(1)).findByContestAndKey(contest, "propositionKey");
        verifyNoInteractions(optionRepository);

    }

    @Test
    void save_optionAbsent_exceptionThrown() {
        final OptionChanged optionChanged = mock(OptionChanged.class);
        when(optionChanged.contestKey()).thenReturn("12245b7ae4b58d21f6e395229b43f1e7");
        when(optionChanged.propositionKey()).thenReturn("propositionKey");
        when(optionChanged.optionKey()).thenReturn("optionKey");
        final Contest contest = mock(Contest.class);
        final Proposition proposition = mock(Proposition.class);
        when(contestService.findByKey(any())).thenReturn(Optional.of(contest));
        when(propositionRepository.findByContestAndKey(eq(contest), any())).thenReturn(Optional.of(proposition));
        when(optionRepository.findByPropositionAndKey(eq(proposition), any())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> optionService.save(optionChanged));
        verify(contestService, times(1)).findByKey("12245b7ae4b58d21f6e395229b43f1e7");
        verifyNoMoreInteractions(contestService);
        verify(propositionRepository, times(1)).findByContestAndKey(contest, "propositionKey");
        verify(optionRepository, times(1)).findByPropositionAndKey(proposition, "optionKey");
        verifyNoMoreInteractions(optionRepository);

    }

    @Test
    void save_ok() {
        final OptionChanged optionChanged = mock(OptionChanged.class);
        when(optionChanged.contestKey()).thenReturn("12245b7ae4b58d21f6e395229b43f1e7");
        when(optionChanged.propositionKey()).thenReturn("propositionKey");
        when(optionChanged.optionKey()).thenReturn("optionKey");
        when(optionChanged.name()).thenReturn("name");
        final Contest contest = mock(Contest.class);
        final Proposition proposition = mock(Proposition.class);

        when(contestService.findByKey(any())).thenReturn(Optional.of(contest));
        when(propositionRepository.findByContestAndKey(eq(contest), any())).thenReturn(Optional.of(proposition));

        final com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option existing =
                mock(com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option.class);
        when(optionRepository.findByPropositionAndKey(eq(proposition), any())).thenReturn(Optional.of(existing));

        final com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option expectedEntity = mock(
                com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option.class);
        when(optionRepository.save(existing)).thenReturn(expectedEntity);
        optionService.save(optionChanged);
        verify(contestService, times(1)).findByKey("12245b7ae4b58d21f6e395229b43f1e7");
        verifyNoMoreInteractions(contestService);
        verify(propositionRepository, times(1)).findByContestAndKey(contest, "propositionKey");
        InOrder inOrder = Mockito.inOrder(existing, optionRepository);
        inOrder.verify(optionRepository).findByPropositionAndKey(proposition, "optionKey");
        inOrder.verify(existing).setName("name");
        inOrder.verify(optionRepository).save(existing);
        verifyNoMoreInteractions(optionRepository);

    }
}
