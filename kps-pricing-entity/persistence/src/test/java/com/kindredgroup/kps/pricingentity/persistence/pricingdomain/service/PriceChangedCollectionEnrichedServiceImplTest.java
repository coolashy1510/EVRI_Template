package com.kindredgroup.kps.pricingentity.persistence.pricingdomain.service;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.*;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.TestHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Price;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.*;
import com.kindredgroup.kps.pricingentity.persistence.pricingdomain.repository.PriceChangedCollectionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;

import static com.kindredgroup.kps.internal.api.OptionType.PARTICIPANT;
import static com.kindredgroup.kps.internal.api.VariantType.PLAIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PriceChangedCollectionEnrichedServiceImplTest {

    public static final String CONTEST_KEY = "12245b7ae4b58d21f6e395229b43f1e7";
    public static final String PROPOSITION_KEY = "propositionKey1";
    public static final String OPTION_KEY = "optionKey1";
    public static final String OPTION_NAME = "optionName";
    public static final OptionType OPTION_TYPE = OptionType.T1;
    public static final String VARIANT_KEY = "variantKey1";
    public static final String VARIANT_NAME = "variantName";
    public static final VariantType VARIANT_TYPE = VariantType.PLAIN;
    public static final String PROPOSITION_TYPE = "1st_half_1x2";
    PriceChangedCollectionEnrichedServiceImpl service;
    AutoCloseable closeable;

    @Mock
    PriceChangedCollectionRepository repository;

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new PriceChangedCollectionEnrichedServiceImpl(repository);
    }

    @AfterEach
    void terminate() throws Exception {
        closeable.close();
    }

    @Test
    void enriched() {
        PriceChangedCollection input = TestHelper.PRICE_CHANGED_COLLECTION_PAYLOAD;
        Contest persisted = getPriceChangedCollectionPersisted();
        when(repository.findByKey(CONTEST_KEY)).thenReturn(persisted.getPropositions());
        PriceChangedCollectionEnriched enriched = service.enriched(input);
        EnrichedPrice priceEnriched = enriched.pricesChanged().get(0).prices().get(0);
        verifyEnrichedResult(enriched, priceEnriched);
    }

    @Test
    void enrichedNotUpdated() {
        PriceChangedCollection input = TestHelper.PRICE_CHANGED_COLLECTION_PAYLOAD;
        Contest persisted = getPriceChangedCollectionPersisted();
        persisted.getPropositions().get(0).setKey("invalidKey");
        when(repository.findByKey(CONTEST_KEY)).thenReturn(persisted.getPropositions());
        PriceChangedCollectionEnriched enriched = service.enriched(input);
        assertAll(
                () -> assertEquals(CONTEST_KEY, enriched.contestKey()),
                () -> assertTrue(enriched.pricesChanged().isEmpty())
        );
    }

    @Test
    void enrichedNoCommonPrices() {
        PriceChangedCollection input = TestHelper.PRICE_CHANGED_COLLECTION_PAYLOAD;
        Contest persisted = getPriceChangedCollectionPersisted();
        persisted.getPropositions().get(0).getPrices().clear();
        when(repository.findByKey(CONTEST_KEY)).thenReturn(persisted.getPropositions());
        PriceChangedCollectionEnriched enriched = service.enriched(input);
        EnrichedPrice priceEnriched = enriched.pricesChanged().get(0).prices().get(0);
        verifyEnrichedResult(enriched, priceEnriched);
    }

    @Test
    void enrichedNotUpdatedEmptyDB() {
        PriceChangedCollection input = TestHelper.PRICE_CHANGED_COLLECTION_PAYLOAD;
        Contest persisted = getPriceChangedCollectionPersisted();
        persisted.getPropositions().clear();
        when(repository.findByKey(CONTEST_KEY)).thenReturn(persisted.getPropositions());
        assertNull(service.enriched(input));
    }

    @Test
    void enrichedPriceChangedCollectionNotFound() {
        PriceChangedCollection input = PriceChangedCollection.builder().contestKey(CONTEST_KEY).build();
        when(repository.findByKey(CONTEST_KEY)).thenReturn(List.of());
        assertNull(service.enriched(input));
    }

    @Test
    void enrichedPriceChangedCollection_PlayerKey() {
        PriceChangedCollection input = TestHelper.PRICE_CHANGED_COLLECTION_PAYLOAD_WITH_PLAYERS;
        Contest persisted = getPriceChangedCollectionPersisted();
        persisted.getPropositions().get(0).getPrices().clear();
        Proposition proposition = getProposition("anytime_goalscorer", "anytime_goalscorer");
        Option option = getOption("alex_paulo_menezes_santana", proposition, PARTICIPANT);
        proposition.getOptions().add(option);
        List<OptionEntity> entities = List.of(new OptionEntity(option, "6QC0AZ2IEYKJ9FY2FAPTJQZ7Z", "Participant"));
        option.setOptionEntities(entities);
        Variant variant = getVariant("plain", proposition, PLAIN);
        proposition.getVariants().add(variant);
        Price price = new Price(proposition.getOptions().get(0), proposition.getVariants().get(0), new BigDecimal(10));
        proposition.getPrices().add(price);
        persisted.getPropositions().add(proposition);
        when(repository.findByKey(CONTEST_KEY)).thenReturn(persisted.getPropositions());
        PriceChangedCollectionEnriched enriched = service.enriched(input);
        EnrichedPrice enrichedPrice = enriched.pricesChanged().get(0).prices().get(0);

        assertEquals("alex_paulo_menezes_santana", enrichedPrice.optionKey());
        assertEquals("plain", enrichedPrice.variantKey());
        assertEquals(
                QuantFieldName.PLAYER_KEY,
                enrichedPrice.quantContractType().fields().stream().map(QuantField::name).toList().get(0));
        assertEquals(
                "6QC0AZ2IEYKJ9FY2FAPTJQZ7Z",
                enrichedPrice.quantContractType().fields().stream().map(QuantField::value).toList().get(0));
        assertEquals(QuantMarketTypeClassName.ANY_TIME_GOAL_SCORER, enrichedPrice.quantMarketType().className());
        assertEquals(QuantContractTypeClassName.PLAYER, enrichedPrice.quantContractType().className());
        assertEquals(BigDecimal.valueOf(9.99), enrichedPrice.price());
    }

    @Test
    void enrichedPriceChangedCollection_NoPlayerKey() {
        PriceChangedCollection input = TestHelper.PRICE_CHANGED_COLLECTION_PAYLOAD_WITH_PLAYERS;
        Contest persisted = getPriceChangedCollectionPersisted();
        Proposition proposition = getProposition("anytime_goalscorer", "anytime_goalscorer");
        Option option = getOption("alex_paulo_menezes_santana", proposition, PARTICIPANT);
        proposition.getOptions().add(option);
        Variant variant = getVariant("plain", proposition, PLAIN);
        proposition.getVariants().add(variant);
        Price price = new Price(proposition.getOptions().get(0), proposition.getVariants().get(0), new BigDecimal(10));
        proposition.getPrices().add(price);
        persisted.getPropositions().add(proposition);
        when(repository.findByKey(CONTEST_KEY)).thenReturn(persisted.getPropositions());
        PriceChangedCollectionEnriched enriched = service.enriched(input);
        assertTrue(enriched.pricesChanged().isEmpty());
    }


    @Test
    void enrichedPriceChangedCollection_EmptyPlayerKey() {
        PriceChangedCollection input = TestHelper.PRICE_CHANGED_COLLECTION_PAYLOAD_WITH_PLAYERS;
        Contest persisted = getPriceChangedCollectionPersisted();
        Proposition proposition = getProposition("anytime_goalscorer", "anytime_goalscorer");
        Option option = getOption("alex_paulo_menezes_santana", proposition, PARTICIPANT);
        proposition.getOptions().add(option);
        List<OptionEntity> entities = List.of();
        option.setOptionEntities(entities);
        Variant variant = getVariant("plain", proposition, PLAIN);
        proposition.getVariants().add(variant);
        Price price = new Price(proposition.getOptions().get(0), proposition.getVariants().get(0), new BigDecimal(10));
        proposition.getPrices().add(price);
        persisted.getPropositions().add(proposition);
        when(repository.findByKey(CONTEST_KEY)).thenReturn(persisted.getPropositions());
        PriceChangedCollectionEnriched enriched = service.enriched(input);
        assertTrue(enriched.pricesChanged().isEmpty());
    }

    @Test
    void enrichedPriceChangedCollection_MultiplePlayerKeys() {
        PriceChangedCollection input = TestHelper.PRICE_CHANGED_COLLECTION_PAYLOAD_WITH_PLAYERS;
        Contest persisted = getPriceChangedCollectionPersisted();
        Proposition proposition = getProposition("anytime_goalscorer", "anytime_goalscorer");
        Option option = getOption("alex_paulo_menezes_santana", proposition, PARTICIPANT);
        proposition.getOptions().add(option);
        List<OptionEntity> entities = List.of(new OptionEntity(option, "6QC0AZ2IEYKJ9FY2FAPTJQZ7Z", "Participant"),
                new OptionEntity(option, "13JX465YQRS2VE7F7Q7C29GB3", "Participant"));
        option.setOptionEntities(entities);
        Variant variant = getVariant("plain", proposition, PLAIN);
        proposition.getVariants().add(variant);
        Price price = new Price(proposition.getOptions().get(0), proposition.getVariants().get(0), new BigDecimal(10));
        proposition.getPrices().add(price);
        persisted.getPropositions().add(proposition);
        when(repository.findByKey(CONTEST_KEY)).thenReturn(persisted.getPropositions());
        PriceChangedCollectionEnriched enriched = service.enriched(input);
        assertTrue(enriched.pricesChanged().isEmpty());
    }

    private static void verifyEnrichedResult(PriceChangedCollectionEnriched enriched, EnrichedPrice priceEnriched) {
        assertAll(
                () -> assertEquals(CONTEST_KEY, enriched.contestKey()),
                () -> assertFalse(enriched.pricesChanged().isEmpty()),
                () -> assertEquals(PROPOSITION_KEY, enriched.pricesChanged().get(0).propositionKey()),
                () -> assertFalse(enriched.pricesChanged().get(0).prices() == null
                        || enriched.pricesChanged().get(0).prices().isEmpty()),
                () -> assertEquals(BigDecimal.valueOf(9.99), priceEnriched.price()),
                () -> assertEquals(OPTION_KEY, priceEnriched.optionKey()),
                () -> assertEquals(QuantContractTypeClassName.TEAM_ONE, priceEnriched.quantContractType().className()),
                () -> assertEquals(VARIANT_KEY, priceEnriched.variantKey()),
                () -> assertEquals(QuantMarketTypeClassName.FIRST_HALF_WIN_DRAW_WIN,
                        enriched.pricesChanged().get(0).prices().get(0).quantMarketType().className())
        );
    }


    private static Contest getPriceChangedCollectionPersisted() {
        var result = new Contest();
        Proposition proposition = getProposition(PROPOSITION_KEY, PROPOSITION_TYPE);
        proposition.getOptions().add(getOption(OPTION_KEY, proposition, OPTION_TYPE));
        proposition.getVariants().add(getVariant(VARIANT_KEY, proposition, VARIANT_TYPE));
        proposition.setPrices(List.of(getPrice(proposition)));
        result.getPropositions().add(proposition);
        return result;
    }

    private static Proposition getProposition(String key, String type) {
        Proposition result = new Proposition();
        result.setKey(key);
        result.setType(type);
        return result;
    }

    private static Price getPrice(Proposition proposition) {
        return new Price(getOption(OPTION_KEY, proposition, OPTION_TYPE), getVariant(VARIANT_KEY, proposition, VARIANT_TYPE),
                BigDecimal.ONE);
    }

    private static Option getOption(String key, Proposition proposition, OptionType type) {
        Option result = new Option(key, proposition, type);
        result.setName(OPTION_NAME);
        return result;
    }

    private static Variant getVariant(String key, Proposition proposition, VariantType type) {
        Variant result = new Variant(key, proposition, type);
        result.setName(VARIANT_NAME);
        return result;
    }
}
