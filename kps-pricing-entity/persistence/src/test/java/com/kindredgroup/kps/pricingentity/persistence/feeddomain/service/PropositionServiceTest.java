package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionManuallyControlledField;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.TestHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.mapper.FeedDomainMapper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.PropositionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PropositionServiceTest {
    public static final com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition PROPOSITION =
            TestHelper.PROPOSITION_PAYLOAD;
    public static final com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2 PROPOSITION_V2 =
            TestHelper.PROPOSITION_PAYLOAD_V2;
    @Captor ArgumentCaptor<Proposition> propositionCaptor;
    @Mock private ContestServiceImpl contestService;
    @Mock(strictness = Mock.Strictness.LENIENT) private PropositionRepository propositionRepository;
    private PropositionServiceImpl propositionService;

    private static ArgumentMatcher<Proposition> getPropositionMatcher(Contest expected) {
        return argument -> null == argument.getId()
                && PROPOSITION.propositionKey().equals(argument.getKey())
                && PROPOSITION.propositionType().equals(argument.getType())
                && PROPOSITION.name().equals(argument.getName())
                && expected.equals(argument.getContest())
                && argument.getOptions().isEmpty()
                && argument.getVariants().isEmpty();
    }

    @BeforeEach
    void setUp() {
        propositionService = new PropositionServiceImpl(contestService, propositionRepository);
    }

    @Test
    void get_ok() {
        Contest contest = mock(Contest.class);
        Optional<Proposition> proposition = mock(Optional.class);
        when(propositionRepository.findByContestAndKey(contest, "proposition key")).thenReturn(proposition);
        final Optional<Proposition> result = propositionService.get(contest, "proposition key");
        Assertions.assertEquals(proposition, result);
        verify(propositionRepository, times(1)).findByContestAndKey(any(), any());
        verifyNoMoreInteractions(propositionRepository);
    }

    @Test
    void save_noContest_exceptionThrown() {
        com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition proposition = mock(
                com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition.class);
        final String key = "key";
        when(proposition.contestKey()).thenReturn(key);
        when(contestService.findByKey("key")).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> propositionService.save(proposition));
        verifyNoInteractions(propositionRepository);
    }

    @Test
    void save_propositionPresent_changed() {
        Contest contest = TestHelper.createContest(ContestStatus.PRE_GAME, ContestType.FOOTBALL);
        contest.setKey(PROPOSITION.contestKey());
        Proposition proposition = TestHelper.createProposition(contest);
        proposition.setKey(PROPOSITION.propositionKey());
        contest.getPropositions().add(proposition);

        when(contestService.findByKey(PROPOSITION.contestKey())).thenReturn(Optional.of(contest));

        propositionService.save(PROPOSITION);
        verify(propositionRepository, times(1)).save(proposition);
    }

    @Test
    void save_propositionPresent_notChanged() {
        Contest contest = TestHelper.createContest(ContestStatus.PRE_GAME, ContestType.FOOTBALL);
        Proposition proposition = TestHelper.createProposition(contest);
        contest.getPropositions().add(proposition);
        com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition payload =
                com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition.builder().
                                                                                   propositionType(
                                                                                           proposition.getType())
                                                                                   .name(proposition.getName())
                                                                                   .propositionKey(
                                                                                           proposition.getKey())
                                                                                   .contestKey(
                                                                                           contest.getKey())
                                                                                   .options(new ArrayList<>())
                                                                                   .variants(new ArrayList<>())
                                                                                   .build();

        when(contestService.findByKey(payload.contestKey())).thenReturn(Optional.of(contest));

        propositionService.save(payload);
        verify(propositionRepository, never()).save(any());
    }

    @Test
    void save_created() {
        Contest contest = mock(Contest.class);
        Proposition proposition = mock(Proposition.class);
        when(proposition.getKey()).thenReturn("otherPropositionKey");
        when(contest.getPropositions()).thenReturn(List.of(proposition));
        when(contestService.findByKey(PROPOSITION.contestKey())).thenReturn(Optional.of(contest));

        try (MockedStatic<OptionServiceImpl> optionService = mockStatic(OptionServiceImpl.class);
                MockedStatic<VariantServiceImpl> variantService = mockStatic(VariantServiceImpl.class);
                MockedStatic<FeedDomainMapper> mockedMapper = mockStatic(FeedDomainMapper.class)) {
            final List<Option> options = new ArrayList<>();
            final List<Variant> variants = new ArrayList<>();
            options.add(mock(Option.class));
            variants.add(mock(Variant.class));
            optionService.when(
                                 () -> OptionServiceImpl.convert(eq(PROPOSITION.options()),
                                         argThat(getPropositionMatcher(contest))))
                         .thenReturn(options);
            variantService.when(
                                  () -> VariantServiceImpl.convert(eq(PROPOSITION.variants()),
                                          argThat(getPropositionMatcher(contest))))
                          .thenReturn(variants);

            final Proposition saved = mock(Proposition.class);

            when(propositionRepository.save(any())).thenReturn(saved);
            propositionService.save(PROPOSITION);
            verify(propositionRepository, only()).save(propositionCaptor.capture());

            assertNull(propositionCaptor.getValue().getId());
            assertEquals(PROPOSITION.propositionKey(), propositionCaptor.getValue().getKey());
            assertEquals(PROPOSITION.propositionType(), propositionCaptor.getValue().getType());
            assertEquals(PROPOSITION.name(), propositionCaptor.getValue().getName());
            assertEquals(contest, propositionCaptor.getValue().getContest());

            assertEquals(options.size(), propositionCaptor.getValue().getOptions().size());
            assertTrue(options.containsAll(propositionCaptor.getValue().getOptions()));
            assertEquals(variants.size(), propositionCaptor.getValue().getVariants().size());
            assertTrue(variants.containsAll(propositionCaptor.getValue().getVariants()));
            assertNotNull(propositionCaptor.getValue().getOptions());

        }
    }

    @Test
    void update_noContest_exceptionThrown() {
        PropositionChanged propositionChanged = mock(
                PropositionChanged.class);
        final String key = "key";
        when(propositionChanged.contestKey()).thenReturn(key);
        when(contestService.findByKey("key")).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> propositionService.update(propositionChanged));
        verifyNoInteractions(propositionRepository);
    }

    @Test
    void update_ok() {
        var propositionChanged = TestHelper.PROPOSITION_CHANGED_PAYLOAD;
        Contest contest = mock(Contest.class);
        final com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition expectedResult = mock(
                com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition.class);
        final Proposition saved = mock(Proposition.class);
        Proposition proposition = mock(Proposition.class);
        when(proposition.getKey()).thenReturn(propositionChanged.propositionKey() + "123");
        Proposition matchingProposition = mock(Proposition.class);
        when(matchingProposition.getKey()).thenReturn(propositionChanged.propositionKey());

        when(contest.getPropositions()).thenReturn(List.of(proposition, matchingProposition));
        when(contestService.findByKey(propositionChanged.contestKey())).thenReturn(Optional.of(contest));
        when(propositionRepository.save(matchingProposition)).thenReturn(saved);
        propositionService.update(propositionChanged);

        verify(matchingProposition, times(1)).getKey();
        verify(matchingProposition, times(1)).setName(propositionChanged.name());
        verifyNoMoreInteractions(matchingProposition);
        verify(propositionRepository, times(1)).save(matchingProposition);
        verifyNoMoreInteractions(propositionRepository);
    }

    @Test
    void update_ok_with_null_name() {
        var propositionChanged = PropositionChanged.builder()
                                                   .contestKey("12245b7ae4b58d21f6e395229b43f1e7")
                                                   .propositionKey("propositionKey")
                                                   .provider(FeedProvider.RUNNING_BALL)
                                                   .bettingOpen(true)
                                                   .cancelled(false)
                                                   .cashOutOpen(true)
                                                   .manuallyControlledFields(Set.of(
                                                           PropositionManuallyControlledField.NAME,
                                                           PropositionManuallyControlledField.BETTING_OPEN))
                                                   .build();
        Contest contest = mock(Contest.class);
        final com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition expectedResult = mock(
                com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition.class);
        Proposition proposition = mock(Proposition.class);
        when(proposition.getKey()).thenReturn(propositionChanged.propositionKey() + "123");
        Proposition matchingProposition = mock(Proposition.class);
        when(matchingProposition.getKey()).thenReturn(propositionChanged.propositionKey());

        when(contest.getPropositions()).thenReturn(List.of(proposition, matchingProposition));
        when(contestService.findByKey(propositionChanged.contestKey())).thenReturn(Optional.of(contest));

        propositionService.update(propositionChanged);

        verify(matchingProposition, times(1)).getKey();
        verify(matchingProposition, times(0)).setName(propositionChanged.name());
        verifyNoMoreInteractions(matchingProposition);
        verify(propositionRepository, times(0)).save(matchingProposition);
        verifyNoMoreInteractions(propositionRepository);
    }

    @Test
    void update_noProposition_exceptionThrown() {
        var propositionChanged = TestHelper.PROPOSITION_CHANGED_PAYLOAD;
        Contest contest = mock(Contest.class);
        Proposition proposition = mock(Proposition.class);
        when(proposition.getKey()).thenReturn(propositionChanged.propositionKey() + "123");
        when(contest.getPropositions()).thenReturn(List.of(proposition));
        when(contestService.findByKey(PROPOSITION.contestKey())).thenReturn(Optional.of(contest));
        assertThrows(IllegalStateException.class, () -> propositionService.update(propositionChanged));
        verifyNoInteractions(propositionRepository);
    }

    @Test
    void save_v2NoContest_exceptionThrown() {
        com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2 proposition = mock(
                com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2.class);
        final String key = "key";
        when(proposition.contestKey()).thenReturn(key);
        when(contestService.findByKey("key")).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> propositionService.save(proposition));
        verifyNoInteractions(propositionRepository);
    }

    @Test
    void save_v2PropositionPresent_changed() {
        Contest contest = TestHelper.createContest(ContestStatus.PRE_GAME, ContestType.FOOTBALL);
        contest.setKey(PROPOSITION_V2.contestKey());
        Proposition proposition = TestHelper.createProposition(contest);
        proposition.setKey(PROPOSITION_V2.propositionKey());
        contest.getPropositions().add(proposition);

        when(contestService.findByKey(PROPOSITION_V2.contestKey())).thenReturn(Optional.of(contest));

        propositionService.save(PROPOSITION_V2);
        verify(propositionRepository, times(1)).save(proposition);
    }

    @Test
    void save_v2PropositionPresent_notChanged() {
        Contest contest = TestHelper.createContest(ContestStatus.PRE_GAME, ContestType.FOOTBALL);
        Proposition proposition = TestHelper.createProposition(contest);
        contest.getPropositions().add(proposition);
        com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2 payload =
                com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2.builder().
                                                                                     propositionType(
                                                                                             proposition.getType())
                                                                                     .name(proposition.getName())
                                                                                     .propositionKey(
                                                                                             proposition.getKey())
                                                                                     .contestKey(
                                                                                             contest.getKey())
                                                                                     .options(new ArrayList<>())
                                                                                     .variants(new ArrayList<>())
                                                                                     .build();

        when(contestService.findByKey(payload.contestKey())).thenReturn(Optional.of(contest));

        propositionService.save(payload);
        verify(propositionRepository, never()).save(any());
    }

    @Test
    void save_v2_created() {
        Contest contest = mock(Contest.class);
        Proposition proposition = mock(Proposition.class);
        when(proposition.getKey()).thenReturn("otherPropositionKey");
        when(contest.getPropositions()).thenReturn(List.of(proposition));
        when(contestService.findByKey(PROPOSITION_V2.contestKey())).thenReturn(Optional.of(contest));

        try (MockedStatic<OptionServiceImpl> optionService = mockStatic(OptionServiceImpl.class);
                MockedStatic<VariantServiceImpl> variantService = mockStatic(VariantServiceImpl.class);
                MockedStatic<FeedDomainMapper> mockedMapper = mockStatic(FeedDomainMapper.class)) {
            final List<Option> options = new ArrayList<>();
            final List<Variant> variants = new ArrayList<>();
            options.add(mock(Option.class));
            variants.add(mock(Variant.class));
            optionService.when(
                                 () -> OptionServiceImpl.convertV2(eq(PROPOSITION_V2.options()),
                                         argThat(getPropositionMatcher(contest))))
                         .thenReturn(options);
            variantService.when(
                                  () -> VariantServiceImpl.convert(eq(PROPOSITION_V2.variants()),
                                          argThat(getPropositionMatcher(contest))))
                          .thenReturn(variants);

            final Proposition saved = mock(Proposition.class);

            when(propositionRepository.save(any())).thenReturn(saved);
            propositionService.save(PROPOSITION_V2);
            verify(propositionRepository, only()).save(propositionCaptor.capture());

            assertNull(propositionCaptor.getValue().getId());
            assertEquals(PROPOSITION_V2.propositionKey(), propositionCaptor.getValue().getKey());
            assertEquals(PROPOSITION_V2.propositionType(), propositionCaptor.getValue().getType());
            assertEquals(PROPOSITION_V2.name(), propositionCaptor.getValue().getName());
            assertEquals(contest, propositionCaptor.getValue().getContest());

            assertEquals(options.size(), propositionCaptor.getValue().getOptions().size());
            assertTrue(options.containsAll(propositionCaptor.getValue().getOptions()));
            assertEquals(variants.size(), propositionCaptor.getValue().getVariants().size());
            assertTrue(variants.containsAll(propositionCaptor.getValue().getVariants()));
            assertNotNull(propositionCaptor.getValue().getOptions());

        }
    }
}
