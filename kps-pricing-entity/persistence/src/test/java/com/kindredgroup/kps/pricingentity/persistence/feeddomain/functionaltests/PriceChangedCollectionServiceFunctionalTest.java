package com.kindredgroup.kps.pricingentity.persistence.feeddomain.functionaltests;

import java.math.BigDecimal;
import java.util.List;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import com.kindredgroup.kps.internal.api.pricingdomain.OutcomePricesChanged;
import com.kindredgroup.kps.internal.api.pricingdomain.Price;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollectionEnriched;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.PropositionRepository;
import com.kindredgroup.kps.pricingentity.persistence.pricingdomain.repository.PriceChangedCollectionRepository;
import com.kindredgroup.kps.pricingentity.persistence.pricingdomain.service.PriceChangedCollectionEnrichedServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Transactional
class PriceChangedCollectionServiceFunctionalTest extends AbstractFunctionalTest {

    @Autowired
    protected ContestRepository contestRepository;
    @Autowired
    protected PropositionRepository propositionRepository;
    @Autowired
    protected PriceChangedCollectionRepository priceChangedCollectionRepository;
    private PriceChangedCollectionEnrichedServiceImpl priceChangedCollectionEnrichedService;

    private static PriceChangedCollection createPriceChangedCollection() {
        final PriceChangedCollection.PriceChangedCollectionBuilder builder = PriceChangedCollection.builder();
        return builder
                .contestKey("12245b7ae4b58d21f6e395229b43f1e7")
                .provider(FeedProvider.BET_RADAR)
                .source("source")
                .pricesChanged(List.of(
                        OutcomePricesChanged.builder().propositionKey("1x2").prices(List.of(
                                                    Price.builder().price(BigDecimal.valueOf(9.99)).optionKey("team1")
                                                         .variantKey("plain").build(),
                                                    Price.builder().price(BigDecimal.valueOf(4.99)).optionKey("team2")
                                                         .variantKey("plain").build()))
                                            .build())).build();
    }

    @BeforeEach
    void setUp() {
        priceChangedCollectionEnrichedService = new PriceChangedCollectionEnrichedServiceImpl(priceChangedCollectionRepository);
    }

    @Test
    void get_ok() {
        Contest preGame = new Contest();
        preGame.setKey("12245b7ae4b58d21f6e395229b43f1e7");
        preGame.setType(ContestType.FOOTBALL);
        preGame.setStatus(ContestStatus.PRE_GAME);
        preGame.setName("contestName");
        contestRepository.save(preGame);

        final com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition proposition =
                new com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition();
        proposition.setName("propositionName");
        proposition.setContest(preGame);
        proposition.setType("1x2");
        proposition.setKey("1x2");
        Option option = new Option("team1", proposition, OptionType.T1);
        option.setName("optionName");
        proposition.getOptions().add(option);
        Variant variant = new Variant("plain", proposition, VariantType.PLAIN);
        variant.setName("variantName");
        proposition.getVariants().add(variant);
        propositionRepository.save(proposition);

        PriceChangedCollection priceChangedCollection = createPriceChangedCollection();
        PriceChangedCollectionEnriched enriched = priceChangedCollectionEnrichedService.enriched(priceChangedCollection);
        assertFalse(enriched.pricesChanged().isEmpty());
    }

}
