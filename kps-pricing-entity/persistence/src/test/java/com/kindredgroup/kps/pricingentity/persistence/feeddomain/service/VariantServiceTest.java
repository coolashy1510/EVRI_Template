package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Variant;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantChanged;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.PropositionRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.VariantRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VariantServiceTest {

    public static final String PROPOSITION_KEY = "propositionKey";
    @Mock
    private ContestServiceImpl contestService;
    private VariantServiceImpl variantService;
    @Mock
    private VariantRepository variantRepository;
    @Mock
    private PropositionRepository propositionRepository;

    @BeforeEach
    void setUp() {
        variantService = new VariantServiceImpl(variantRepository, contestService, propositionRepository);
    }

    @Test
    void convert_ok() {

        List<Variant> variants = new ArrayList<>();
        variants.add(Variant.builder().variantKey("key1").variantType(VariantType.LINE).name("name1").build());
        variants.add(Variant.builder().variantKey("key2").variantType(VariantType.MARGIN).name("name2").build());

        Proposition proposition = mock(Proposition.class);
        final List<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant> result =
                VariantServiceImpl.convert(
                        variants,
                        proposition);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(
                variant -> variant.getName().equals("name1") && variant.getProposition().equals(proposition) &&
                        variant.getKey().equals("key1") && variant.getType().equals(VariantType.LINE)));
        assertTrue(result.stream().anyMatch(
                variant -> variant.getName().equals("name2") && variant.getProposition().equals(proposition) &&
                        variant.getKey().equals("key2") && variant.getType().equals(VariantType.MARGIN)));


    }

    @Test
    void convert_emptyList_ok() {
        Proposition proposition = mock(Proposition.class);
        final List<com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant> result = VariantServiceImpl.convert(
                List.of(), proposition);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_contestAbsent_exceptionThrown() {
        final VariantChanged variantChanged = mock(VariantChanged.class);
        when(variantChanged.contestKey()).thenReturn("key");
        when(contestService.findByKey(any())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> variantService.save(variantChanged));
        verify(contestService, times(1)).findByKey("key");
        verifyNoInteractions(variantRepository);

    }

    @Test
    void save_propositionAbsent_exceptionThrown() {
        final VariantChanged variantChanged = mock(VariantChanged.class);
        when(variantChanged.contestKey()).thenReturn("12245b7ae4b58d21f6e395229b43f1e7");
        when(variantChanged.propositionKey()).thenReturn(PROPOSITION_KEY);
        final Contest contest = mock(Contest.class);
        when(contestService.findByKey(any())).thenReturn(Optional.of(contest));
        when(propositionRepository.findByContestAndKey(eq(contest), any())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> variantService.save(variantChanged));
        verify(contestService, times(1)).findByKey("12245b7ae4b58d21f6e395229b43f1e7");
        verifyNoMoreInteractions(contestService);
        verify(propositionRepository, times(1)).findByContestAndKey(contest, PROPOSITION_KEY);
        verifyNoInteractions(variantRepository);

    }

    @Test
    void save_variantAbsent_exceptionThrown() {
        final VariantChanged variantChanged = mock(VariantChanged.class);
        when(variantChanged.contestKey()).thenReturn("12245b7ae4b58d21f6e395229b43f1e7");
        when(variantChanged.propositionKey()).thenReturn(PROPOSITION_KEY);
        when(variantChanged.variantKey()).thenReturn("variantKey");
        final Contest contest = mock(Contest.class);
        final Proposition proposition = mock(Proposition.class);
        when(contestService.findByKey(any())).thenReturn(Optional.of(contest));
        when(propositionRepository.findByContestAndKey(eq(contest), any())).thenReturn(Optional.of(proposition));
        when(variantRepository.findByPropositionAndKey(eq(proposition), any())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> variantService.save(variantChanged));
        verify(contestService, times(1)).findByKey("12245b7ae4b58d21f6e395229b43f1e7");
        verifyNoMoreInteractions(contestService);
        verify(propositionRepository, times(1)).findByContestAndKey(contest, PROPOSITION_KEY);
        verify(variantRepository, times(1)).findByPropositionAndKey(proposition, "variantKey");
        verifyNoMoreInteractions(variantRepository);

    }

    @Test
    void save_ok() {
        final VariantChanged variantChanged = mock(VariantChanged.class);
        when(variantChanged.contestKey()).thenReturn("12245b7ae4b58d21f6e395229b43f1e7");
        when(variantChanged.propositionKey()).thenReturn(PROPOSITION_KEY);
        when(variantChanged.variantKey()).thenReturn("variantKey");
        when(variantChanged.name()).thenReturn("name");

        final Contest contest = mock(Contest.class);
        when(contestService.findByKey(any())).thenReturn(Optional.of(contest));
        Proposition proposition = mock(Proposition.class);
        when(propositionRepository.findByContestAndKey(eq(contest), any())).thenReturn(Optional.of(proposition));

        final com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant existing =
                mock(com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant.class);
        when(variantRepository.findByPropositionAndKey(eq(proposition), any())).thenReturn(Optional.of(existing));

        final com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant saved = mock(
                com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant.class);
        when(variantRepository.save(existing)).thenReturn(saved);

        variantService.save(variantChanged);
        verify(contestService, times(1)).findByKey("12245b7ae4b58d21f6e395229b43f1e7");
        verifyNoMoreInteractions(contestService);
        verify(propositionRepository, times(1)).findByContestAndKey(contest, "propositionKey");
        InOrder inOrder = Mockito.inOrder(existing, variantRepository);
        inOrder.verify(variantRepository).findByPropositionAndKey(proposition, "variantKey");
        inOrder.verify(existing).setName("name");
        inOrder.verify(variantRepository).save(existing);
        verifyNoMoreInteractions(variantRepository);

    }


}
