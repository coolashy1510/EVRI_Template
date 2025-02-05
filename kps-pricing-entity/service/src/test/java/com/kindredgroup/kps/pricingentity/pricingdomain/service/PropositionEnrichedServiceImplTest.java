package com.kindredgroup.kps.pricingentity.pricingdomain.service;


import java.util.List;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.Argument;
import com.kindredgroup.kps.internal.api.pricingdomain.Entity;
import com.kindredgroup.kps.internal.api.pricingdomain.EntityType;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import com.kindredgroup.kps.internal.api.pricingdomain.PropositionEnriched;
import com.kindredgroup.kps.internal.api.pricingdomain.QuantContractTypeClassName;
import com.kindredgroup.kps.internal.api.pricingdomain.QuantMarketTypeClassName;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Option;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Variant;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.OptionV2;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropositionEnrichedServiceImplTest {

    public static final String CONTEST_KEY = "contestKey";
    AutoCloseable closeable;
    PropositionEnrichedService service;

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);
        service = new PropositionEnrichedServiceImpl();
    }

    @AfterEach
    void terminate() throws Exception {
        closeable.close();
    }

    @Test
    void enriched() {
        final Proposition.PropositionBuilder builder =
                Proposition.builder();
        Proposition input = builder.contestKey(CONTEST_KEY)
                                   .propositionKey("propositionKey")
                                   .cashOutOpen(false)
                                   .name("Total Goals")
                                   .options(List.of(
                                           Option.builder().optionKey("over")
                                                 .optionType(OptionType.TOTAL_OVER)
                                                 .bettingOpen(true)
                                                 .name("Over").build(),
                                           Option.builder().optionKey("under")
                                                 .optionType(OptionType.TOTAL_UNDER)
                                                 .bettingOpen(true)
                                                 .name("Under")
                                                 .build()))
                                   .propositionType("total")
                                   .provider(FeedProvider.BET_RADAR)
                                   .variants(List.of(
                                           Variant.builder().variantKey("47.5")
                                                  .variantType(VariantType.OVER_UNDER)
                                                  .bettingOpen(false)
                                                  .name("47.5")
                                                  .build(),
                                           Variant.builder().variantKey("49.5")
                                                  .variantType(VariantType.OVER_UNDER)
                                                  .bettingOpen(false)
                                                  .name("49.5")
                                                  .build()))
                                   .build();
        PropositionEnriched enriched = service.enriched(input);

        assertAll(
                () -> assertEquals(input.contestKey(), enriched.contestKey()),
                () -> assertEquals(input.options().size() * input.variants().size(), enriched.enrichedMappings().size()),
                () -> assertEquals(input.variants().size(), enriched.enrichedMappings().stream()
                                                                    .filter(enrichedMapping -> enrichedMapping.quantContractType()
                                                                                                              .className()
                                                                                                              .equals(QuantContractTypeClassName.TOTAL_OVER))
                                                                    .toList().size()),
                () -> assertEquals(input.variants().size(), enriched.enrichedMappings().stream()
                                                                    .filter(enrichedMapping -> enrichedMapping.quantContractType()
                                                                                                              .className()
                                                                                                              .equals(QuantContractTypeClassName.TOTAL_UNDER))
                                                                    .toList().size()),
                () -> assertEquals(input.options().size() * input.variants().size(), enriched.enrichedMappings().stream()
                                                                                             .filter(enrichedMapping -> enrichedMapping.quantMarketType()
                                                                                                                                       .className()
                                                                                                                                       .equals(QuantMarketTypeClassName.TOTAL_GOALS))
                                                                                             .toList().size()));
    }

    @Test
    void enrichedNotUpdated() {
        final Proposition.PropositionBuilder builder =
                Proposition.builder();
        Proposition input = builder.contestKey(CONTEST_KEY)
                                   .propositionKey("propositionKey")
                                   .cashOutOpen(false)
                                   .name("name")
                                   .options(List.of(
                                           Option.builder().optionKey("optionKey1")
                                                 .optionType(OptionType.DRAW)
                                                 .bettingOpen(true)
                                                 .name("name2").build(),
                                           Option.builder().optionKey("optionKey2")
                                                 .optionType(OptionType.EVEN)
                                                 .bettingOpen(true)
                                                 .name("name2")
                                                 .build()))
                                   .propositionType("propositionType")
                                   .provider(FeedProvider.BET_RADAR)
                                   .variants(List.of(
                                           Variant.builder().variantKey("variantKey1")
                                                  .variantType(VariantType.LINE)
                                                  .bettingOpen(true)
                                                  .name("name")
                                                  .build())).build();
        assertEquals(0, service.enriched(input).enrichedMappings().size());
    }

    @Test
    void enriched_v2_ok() {
        final PropositionV2.PropositionV2Builder builder =
                PropositionV2.builder();
        PropositionV2 input = builder.contestKey(CONTEST_KEY)
                                     .propositionKey("propositionKey")
                                     .cashOutOpen(false)
                                     .name("Total Goals")
                                     .options(List.of(
                                             OptionV2.builder().optionKey("over")
                                                     .optionType(OptionType.V2_TOTAL_OVER)
                                                     .bettingOpen(true)
                                                     .name("Over").build(),
                                             OptionV2.builder().optionKey("under")
                                                     .optionType(OptionType.V2_TOTAL_UNDER)
                                                     .bettingOpen(true)
                                                     .name("Under")
                                                     .build(),
                                             OptionV2.builder().optionKey("ptcpnt")
                                                     .optionType(OptionType.V2_PARTICIPANT)
                                                     .arguments(
                                                             List.of(new Argument("arg key", "arg value",
                                                                     new Entity("participant key",
                                                                             EntityType.PARTICIPANT.getValue()))))
                                                     .bettingOpen(true)
                                                     .name("Participant option name")
                                                     .build()))
                                     .propositionType("total")
                                     .provider(FeedProvider.BET_RADAR)
                                     .variants(List.of(
                                             Variant.builder().variantKey("47.5")
                                                    .variantType(VariantType.OVER_UNDER)
                                                    .bettingOpen(false)
                                                    .name("47.5")
                                                    .build(),
                                             Variant.builder().variantKey("49.5")
                                                    .variantType(VariantType.OVER_UNDER)
                                                    .bettingOpen(false)
                                                    .name("49.5")
                                                    .build()))
                                     .build();
        PropositionEnriched enriched = service.enriched(input);

        assertAll(
                () -> assertEquals(input.contestKey(), enriched.contestKey()),
                () -> assertEquals(input.options().size() * input.variants().size(), enriched.enrichedMappings().size()),
                () -> assertEquals(input.variants().size(), enriched.enrichedMappings().stream()
                                                                    .filter(enrichedMapping -> enrichedMapping.quantContractType()
                                                                                                              .className()
                                                                                                              .equals(QuantContractTypeClassName.TOTAL_OVER))
                                                                    .toList().size()),
                () -> assertEquals(input.variants().size(), enriched.enrichedMappings().stream()
                                                                    .filter(enrichedMapping -> enrichedMapping.quantContractType()
                                                                                                              .className()
                                                                                                              .equals(QuantContractTypeClassName.TOTAL_UNDER))
                                                                    .toList().size()),
                () -> assertEquals(input.variants().size(), enriched.enrichedMappings().stream()
                                                                    .filter(enrichedMapping -> enrichedMapping.quantContractType()
                                                                                                              .className()
                                                                                                              .equals(QuantContractTypeClassName.PLAYER))
                                                                    .toList().size()),
                () -> assertEquals(input.options().size() * input.variants().size(), enriched.enrichedMappings().stream()
                                                                                             .filter(enrichedMapping -> enrichedMapping.quantMarketType()
                                                                                                                                       .className()
                                                                                                                                       .equals(QuantMarketTypeClassName.TOTAL_GOALS))
                                                                                             .toList().size()));
    }

    @Test
    void enriched_v2NotUpdated() {
        final PropositionV2.PropositionV2Builder builder =
                PropositionV2.builder();
        PropositionV2 input = builder.contestKey(CONTEST_KEY)
                                     .propositionKey("propositionKey")
                                     .cashOutOpen(false)
                                     .name("name")
                                     .options(List.of(
                                             OptionV2.builder().optionKey("optionKey1")
                                                     .optionType(OptionType.V2_DRAW)
                                                     .bettingOpen(true)
                                                     .name("name2").build(),
                                             OptionV2.builder().optionKey("optionKey2")
                                                     .optionType(OptionType.V2_EVEN)
                                                     .bettingOpen(true)
                                                     .name("name2")
                                                     .build()))
                                     .propositionType("propositionType")
                                     .provider(FeedProvider.BET_RADAR)
                                     .variants(List.of(
                                             Variant.builder().variantKey("variantKey1")
                                                    .variantType(VariantType.LINE)
                                                    .bettingOpen(true)
                                                    .name("name")
                                                    .build())).build();
        assertEquals(0, service.enriched(input).enrichedMappings().size());
    }


}
