package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.TestHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Price;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceChangedCollectionServiceTest {
    public static final PriceChangedCollection PRICE_CHANGED_COLLECTION =
            TestHelper.PRICE_CHANGED_COLLECTION_PAYLOAD;
    @Captor
    ArgumentCaptor<Contest> contestArgumentCaptor;
    @Mock
    private ContestServiceImpl contestService;
    @Mock
    private ContestRepository contestRepository;
    private PriceChangedCollectionServiceImpl priceChangedCollectionService;
    private Contest contestDb;

    @BeforeEach
    void setUp() {
        priceChangedCollectionService = new PriceChangedCollectionServiceImpl(contestService, contestRepository);
        contestDb = new Contest();
        contestDb.setType(ContestType.ATHLETICS);
        contestDb.setName("contestName");
        contestDb.setStatus(ContestStatus.CONCLUDED);
        contestDb.setKey("12245b7ae4b58d21f6e395229b43f1e7");
        Proposition proposition = TestHelper.createProposition(contestDb);
        proposition.setKey(PRICE_CHANGED_COLLECTION.pricesChanged().get(0).propositionKey());
        proposition.getOptions().add(new Option("optionKey1", proposition, OptionType.T1));
        proposition.getVariants().add(new Variant("variantKey1", proposition, VariantType.LINE));
        Price price = new Price(proposition.getOptions().get(0), proposition.getVariants().get(0), new BigDecimal(2));
        proposition.getPrices().add(price);
        contestDb.getPropositions().add(proposition);
    }

    @Test
    void save_noProposition_noChangesPerformed() {
        contestDb.getPropositions().remove(0);
        when(contestService.findByKey(PRICE_CHANGED_COLLECTION.contestKey())).thenReturn(Optional.of(contestDb));
        priceChangedCollectionService.savePriceChangedCollection(PRICE_CHANGED_COLLECTION);
        verify(contestService, times(1)).findByKey(PRICE_CHANGED_COLLECTION.contestKey());
        verifyNoInteractions(contestRepository);
    }

    @Test
    void save_priceMissingVariant_noChangesPerformed() {
        Price price = new Price(new Option("optionKey1", contestDb.getPropositions().get(0), OptionType.T1),
                new Variant("variantKeyError", contestDb.getPropositions().get(0), VariantType.LINE), new BigDecimal(2));
        contestDb.getPropositions().get(0).getPrices().remove(0);
        contestDb.getPropositions().get(0).getPrices().add(price);
        Optional<BigDecimal> initialPrice = contestDb.getPropositions().get(0).getPrices().get(0).getPrice();
        when(contestService.findByKey(PRICE_CHANGED_COLLECTION.contestKey())).thenReturn(Optional.of(contestDb));
        when(contestRepository.save(any())).thenReturn(contestDb);
        priceChangedCollectionService.savePriceChangedCollection(PRICE_CHANGED_COLLECTION);
        verify(contestRepository).save(contestArgumentCaptor.capture());
        assertAll(
                () -> assertFalse(contestArgumentCaptor.getValue().getKey().isBlank()),
                () -> assertEquals(contestDb.getKey(), contestArgumentCaptor.getValue().getKey()),
                () -> assertEquals(contestDb.getType(), contestArgumentCaptor.getValue().getType()),
                () -> assertEquals(contestDb.getPropositions().get(0).getKey(),
                        contestArgumentCaptor.getValue().getPropositions().get(0).getKey()),
                () -> assertNotNull(contestArgumentCaptor.getValue().getPropositions().get(0).getPrices()),
                () -> assertEquals(contestArgumentCaptor.getValue().getPropositions().get(0).getOptions().get(0).getKey(),
                        contestArgumentCaptor.getValue().getPropositions().get(0).getPrices().get(0).getOption().getKey()),
                () -> assertEquals(contestArgumentCaptor.getValue().getPropositions().get(0).getPrices().get(0).getPrice(),
                        initialPrice)
        );
    }

    @Test
    void save_priceMissingOption_noChangesPerformed() {
        Price price = new Price(new Option("optionKeyError", contestDb.getPropositions().get(0), OptionType.T1),
                new Variant("variantKey1", contestDb.getPropositions().get(0), VariantType.LINE), new BigDecimal(2));
        contestDb.getPropositions().get(0).getPrices().remove(0);
        contestDb.getPropositions().get(0).getPrices().add(price);
        Optional<BigDecimal> initialPrice = contestDb.getPropositions().get(0).getPrices().get(0).getPrice();
        when(contestService.findByKey(PRICE_CHANGED_COLLECTION.contestKey())).thenReturn(Optional.of(contestDb));
        when(contestRepository.save(any())).thenReturn(contestDb);
        priceChangedCollectionService.savePriceChangedCollection(PRICE_CHANGED_COLLECTION);
        verify(contestRepository).save(contestArgumentCaptor.capture());
        assertAll(
                () -> assertFalse(contestArgumentCaptor.getValue().getKey().isBlank()),
                () -> assertEquals(contestDb.getKey(), contestArgumentCaptor.getValue().getKey()),
                () -> assertEquals(contestDb.getType(), contestArgumentCaptor.getValue().getType()),
                () -> assertEquals(contestDb.getPropositions().get(0).getKey(),
                        contestArgumentCaptor.getValue().getPropositions().get(0).getKey()),
                () -> assertNotNull(contestArgumentCaptor.getValue().getPropositions().get(0).getPrices()),
                () -> assertEquals(contestArgumentCaptor.getValue().getPropositions().get(0).getPrices().get(0).getPrice(),
                        initialPrice)
        );
    }

    @Test
    void save_wrongVariant_noChangesPerformed() {
        contestDb.getPropositions().remove(0);
        Proposition proposition = TestHelper.createProposition(contestDb);
        proposition.setKey(PRICE_CHANGED_COLLECTION.pricesChanged().get(0).propositionKey());
        proposition.getOptions().add(new Option("optionKey1", proposition, OptionType.T1));
        proposition.getVariants().add(new Variant("variantKeyError", proposition, VariantType.LINE));
        Price price = new Price(proposition.getOptions().get(0), proposition.getVariants().get(0), new BigDecimal(2));
        proposition.getPrices().add(price);
        contestDb.getPropositions().add(proposition);
        when(contestService.findByKey(PRICE_CHANGED_COLLECTION.contestKey())).thenReturn(Optional.of(contestDb));
        priceChangedCollectionService.savePriceChangedCollection(PRICE_CHANGED_COLLECTION);
        verify(contestService, times(1)).findByKey(PRICE_CHANGED_COLLECTION.contestKey());
        verifyNoInteractions(contestRepository);
    }

    @Test
    void save_wrongOption_noChangesPerformed() {
        contestDb.getPropositions().remove(0);
        Proposition proposition = TestHelper.createProposition(contestDb);
        proposition.setKey(PRICE_CHANGED_COLLECTION.pricesChanged().get(0).propositionKey());
        proposition.getOptions().add(new Option("optionKeyError", proposition, OptionType.T1));
        proposition.getVariants().add(new Variant("variantKey1", proposition, VariantType.LINE));
        Price price = new Price(proposition.getOptions().get(0), proposition.getVariants().get(0), new BigDecimal(2));
        proposition.getPrices().add(price);
        contestDb.getPropositions().add(proposition);
        when(contestService.findByKey(PRICE_CHANGED_COLLECTION.contestKey())).thenReturn(Optional.of(contestDb));
        priceChangedCollectionService.savePriceChangedCollection(PRICE_CHANGED_COLLECTION);
        verify(contestService, times(1)).findByKey(PRICE_CHANGED_COLLECTION.contestKey());
        verifyNoInteractions(contestRepository);
    }

    @Test
    void save_priceAdded() {
        contestDb.getPropositions().get(0).getPrices().remove(0);
        when(contestService.findByKey(PRICE_CHANGED_COLLECTION.contestKey())).thenReturn(Optional.of(contestDb));
        when(contestRepository.save(any())).thenReturn(contestDb);
        priceChangedCollectionService.savePriceChangedCollection(PRICE_CHANGED_COLLECTION);
        verify(contestRepository).save(contestArgumentCaptor.capture());
        assertAll(
                () -> assertFalse(contestArgumentCaptor.getValue().getKey().isBlank()),
                () -> assertEquals(contestDb.getKey(), contestArgumentCaptor.getValue().getKey()),
                () -> assertEquals(contestDb.getType(), contestArgumentCaptor.getValue().getType()),
                () -> assertEquals(contestDb.getPropositions().get(0).getKey(),
                        contestArgumentCaptor.getValue().getPropositions().get(0).getKey()),
                () -> assertNotNull(contestArgumentCaptor.getValue().getPropositions().get(0).getPrices()),
                () -> assertEquals(contestArgumentCaptor.getValue().getPropositions().get(0).getOptions().get(0).getKey(),
                        contestArgumentCaptor.getValue().getPropositions().get(0).getPrices().get(0).getOption().getKey()),
                () -> assertEquals(contestArgumentCaptor.getValue().getPropositions().get(0).getVariants().get(0).getKey(),
                        contestArgumentCaptor.getValue().getPropositions().get(0).getPrices().get(0).getVariant().getKey()),
                () -> assertNotNull(contestArgumentCaptor.getValue().getPropositions().get(0).getPrices().get(0).getPrice())
        );
        verify(contestRepository, times(1)).save(any());
    }


    @Test
    void save_priceUpdated() {
        when(contestService.findByKey(PRICE_CHANGED_COLLECTION.contestKey())).thenReturn(Optional.of(contestDb));
        when(contestRepository.save(any())).thenReturn(contestDb);
        Optional<BigDecimal> initialPrice = contestDb.getPropositions().get(0).getPrices().get(0).getPrice();
        priceChangedCollectionService.savePriceChangedCollection(PRICE_CHANGED_COLLECTION);
        verify(contestRepository).save(contestArgumentCaptor.capture());
        assertAll(
                () -> assertFalse(contestArgumentCaptor.getValue().getKey().isBlank()),
                () -> assertEquals(contestDb.getKey(), contestArgumentCaptor.getValue().getKey()),
                () -> assertEquals(contestDb.getType(), contestArgumentCaptor.getValue().getType()),
                () -> assertEquals(contestDb.getPropositions().get(0).getKey(),
                        contestArgumentCaptor.getValue().getPropositions().get(0).getKey()),
                () -> assertNotNull(contestArgumentCaptor.getValue().getPropositions().get(0).getPrices()),
                () -> assertEquals(contestArgumentCaptor.getValue().getPropositions().get(0).getOptions().get(0).getKey(),
                        contestArgumentCaptor.getValue().getPropositions().get(0).getPrices().get(0).getOption().getKey()),
                () -> assertEquals(contestArgumentCaptor.getValue().getPropositions().get(0).getVariants().get(0).getKey(),
                        contestArgumentCaptor.getValue().getPropositions().get(0).getPrices().get(0).getVariant().getKey()),
                () -> assertNotEquals(contestArgumentCaptor.getValue().getPropositions().get(0).getPrices().get(0).getPrice(),
                        initialPrice)
        );
        verify(contestRepository, times(1)).save(any());
    }

    @Nested
    class exceptionTest {

        @Test
        void save_noContest_exceptionThrown() {
            PriceChangedCollection priceChangedCollection = mock(
                    PriceChangedCollection.class);
            final String key = "key";
            when(priceChangedCollection.contestKey()).thenReturn(key);
            when(contestService.findByKey("key")).thenReturn(Optional.empty());
            assertThrows(IllegalStateException.class, () -> priceChangedCollectionService.savePriceChangedCollection(priceChangedCollection));
            verifyNoInteractions(contestRepository);
        }
    }

}
