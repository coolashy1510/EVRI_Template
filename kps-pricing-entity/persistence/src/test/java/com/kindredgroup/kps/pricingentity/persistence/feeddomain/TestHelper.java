package com.kindredgroup.kps.pricingentity.persistence.feeddomain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.kindredgroup.kps.internal.api.OptionType;
import com.kindredgroup.kps.internal.api.VariantType;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestManuallyControlledField;
import com.kindredgroup.kps.internal.api.pricingdomain.ContestStatus;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import com.kindredgroup.kps.internal.api.pricingdomain.OutcomePricesChanged;
import com.kindredgroup.kps.internal.api.pricingdomain.Price;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Option;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionManuallyControlledField;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionManuallyControlledField;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Variant;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantManuallyControlledField;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.OptionV2;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;

public class TestHelper {

    public static final com.kindredgroup.kps.internal.api.pricingdomain.Contest CONTEST_PAYLOAD = createContest();
    public static final com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition PROPOSITION_PAYLOAD =
            createProposition();
    public static final com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2 PROPOSITION_PAYLOAD_V2 =
            createPropositionV2();
    public static final PriceChangedCollection PRICE_CHANGED_COLLECTION_PAYLOAD = createPriceChangedCollection();
    public static final PriceChangedCollection PRICE_CHANGED_COLLECTION_PAYLOAD_WITH_PLAYERS =
            createPriceChangedCollectionKeysWithPlayer();
    public static final PropositionChanged PROPOSITION_CHANGED_PAYLOAD = createPropositionChanged();

    private static PriceChangedCollection createPriceChangedCollection() {
        final PriceChangedCollection.PriceChangedCollectionBuilder builder = PriceChangedCollection.builder();
        return builder
                .contestKey("12245b7ae4b58d21f6e395229b43f1e7")
                .provider(FeedProvider.OPTA_SD_API)
                .source("source")
                .pricesChanged(List.of(
                        OutcomePricesChanged.builder().propositionKey("propositionKey1").prices(List.of(
                                                    Price.builder().price(BigDecimal.valueOf(9.99)).optionKey("optionKey1")
                                                         .variantKey("variantKey1").build(),
                                                    Price.builder().price(BigDecimal.valueOf(4.99)).optionKey("optionKey2")
                                                         .variantKey("1:0").build()))
                                            .build(),
                        OutcomePricesChanged.builder().propositionKey("propositionKey2").prices(List.of(
                                                    Price.builder().price(BigDecimal.valueOf(9.49)).optionKey("optionKey3")
                                                         .variantKey("variantKey3")
                                                         .build(),
                                                    Price.builder().price(BigDecimal.valueOf(4.49)).optionKey("optionKey4")
                                                         .variantKey("variantKey4")
                                                         .build()))
                                            .build())).build();
    }

    private static PriceChangedCollection createPriceChangedCollectionKeysWithPlayer() {
        final PriceChangedCollection.PriceChangedCollectionBuilder builder = PriceChangedCollection.builder();
        return builder
                .contestKey("12245b7ae4b58d21f6e395229b43f1e7")
                .provider(FeedProvider.BET_RADAR)
                .source("source")
                .pricesChanged(List.of(
                        OutcomePricesChanged.builder().propositionKey("anytime_goalscorer").prices(List.of(
                                                    Price.builder().price(BigDecimal.valueOf(9.99)).optionKey(
                                                            "alex_paulo_menezes_santana")
                                                         .variantKey("plain").build(),
                                                    Price.builder().price(BigDecimal.valueOf(4.99)).optionKey(
                                                            "gabriel_girotto_franco")
                                                         .variantKey("plain").build()))
                                            .build(),
                        OutcomePricesChanged.builder().propositionKey("propositionKey2").prices(List.of(
                                                    Price.builder().price(BigDecimal.valueOf(9.49)).optionKey("optionKey3")
                                                         .variantKey("variantKey3")
                                                         .build(),
                                                    Price.builder().price(BigDecimal.valueOf(4.49)).optionKey("optionKey4")
                                                         .variantKey("variantKey4")
                                                         .build()))
                                            .build())).build();
    }

    private static PropositionChanged createPropositionChanged() {
        return PropositionChanged.builder()
                                 .contestKey("12245b7ae4b58d21f6e395229b43f1e7")
                                 .propositionKey("propositionKey")
                                 .provider(FeedProvider.RUNNING_BALL)
                                 .bettingOpen(true).cancelled(false)
                                 .cashOutOpen(true).manuallyControlledFields(
                        Set.of(PropositionManuallyControlledField.NAME, PropositionManuallyControlledField.BETTING_OPEN))
                                 .name("name")
                                 .build();
    }

    private static com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition createProposition() {
        final com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition.PropositionBuilder builder =
                com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition.builder();
        return builder.contestKey("12245b7ae4b58d21f6e395229b43f1e7")
                      .propositionKey("propositionKey")
                      .cashOutOpen(false)
                      .name("name")
                      .options(List.of(
                              Option.builder().optionKey("optionKey1")
                                    .optionType(OptionType.DRAW)
                                    .bettingOpen(true)
                                    .name("name2")
                                    .manuallyControlledFields(Set.of(
                                            OptionManuallyControlledField.NAME,
                                            OptionManuallyControlledField.BETTING_OPEN)).build(),
                              Option.builder().optionKey("optionKey2")
                                    .optionType(OptionType.EVEN)
                                    .bettingOpen(true)
                                    .name("name2")
                                    .manuallyControlledFields(Set.of(
                                            OptionManuallyControlledField.NAME,
                                            OptionManuallyControlledField.BETTING_OPEN))
                                    .build()))
                      .propositionType("propositionType")
                      .provider(FeedProvider.BET_RADAR)
                      .variants(List.of(
                              Variant.builder().variantKey("variantKey1")
                                     .variantType(VariantType.LINE)
                                     .bettingOpen(true)
                                     .name("name")
                                     .manuallyControlledFields(Set.of(
                                             VariantManuallyControlledField.NAME,
                                             VariantManuallyControlledField.BETTING_OPEN))
                                     .build(),
                              Variant.builder().variantKey("variantKey2")
                                     .variantType(VariantType.PLAIN)
                                     .bettingOpen(true)
                                     .name("name")
                                     .manuallyControlledFields(Set.of(
                                             VariantManuallyControlledField.NAME,
                                             VariantManuallyControlledField.BETTING_OPEN))
                                     .build()))
                      .manuallyControlledFields(Set.of(
                              PropositionManuallyControlledField.NAME,
                              PropositionManuallyControlledField.BETTING_OPEN))
                      .build();
    }

    private static com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2 createPropositionV2() {
        final com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2.PropositionV2Builder builder =
                com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2.builder();
        return builder.contestKey("12245b7ae4b58d21f6e395229b43f1e7")
                      .propositionKey("propositionKey")
                      .cashOutOpen(false)
                      .name("name")
                      .options(List.of(
                              OptionV2.builder().optionKey("optionKey1")
                                      .optionType(OptionType.DRAW)
                                      .bettingOpen(true)
                                      .name("name2")
                                      .manuallyControlledFields(Set.of(
                                              OptionManuallyControlledField.NAME,
                                              OptionManuallyControlledField.BETTING_OPEN)).build(),
                              OptionV2.builder().optionKey("optionKey2")
                                      .optionType(OptionType.EVEN)
                                      .bettingOpen(true)
                                      .name("name2")
                                      .manuallyControlledFields(Set.of(
                                              OptionManuallyControlledField.NAME,
                                              OptionManuallyControlledField.BETTING_OPEN))
                                      .build()))
                      .propositionType("propositionType")
                      .provider(FeedProvider.BET_RADAR)
                      .variants(List.of(
                              Variant.builder().variantKey("variantKey1")
                                     .variantType(VariantType.LINE)
                                     .bettingOpen(true)
                                     .name("name")
                                     .manuallyControlledFields(Set.of(
                                             VariantManuallyControlledField.NAME,
                                             VariantManuallyControlledField.BETTING_OPEN))
                                     .build(),
                              Variant.builder().variantKey("variantKey2")
                                     .variantType(VariantType.PLAIN)
                                     .bettingOpen(true)
                                     .name("name")
                                     .manuallyControlledFields(Set.of(
                                             VariantManuallyControlledField.NAME,
                                             VariantManuallyControlledField.BETTING_OPEN))
                                     .build()))
                      .manuallyControlledFields(Set.of(
                              PropositionManuallyControlledField.NAME,
                              PropositionManuallyControlledField.BETTING_OPEN))
                      .build();
    }

    private static com.kindredgroup.kps.internal.api.pricingdomain.Contest createContest() {
        final com.kindredgroup.kps.internal.api.pricingdomain.Contest.ContestBuilder builder =
                com.kindredgroup.kps.internal.api.pricingdomain.Contest.builder();
        return builder.contestKey("12245b7ae4b58d21f6e395229b43f1e7")
                      .advertisedDateTimeUtc(OffsetDateTime.now())
                      .category("category")
                      .contestType(ContestType.FOOTBALL.getValue())
                      .name("name")
                      .provider(FeedProvider.BET_GENIUS)
                      .startDateTimeUtc(OffsetDateTime.now())
                      .status(ContestStatus.PRE_GAME)
                      .groupingId("groupingId")
                      .groupingSequence(1)
                      .iso3CountryCode("FRA")
                      .locationName("locationName")
                      .manuallyControlledFields(Set.of(
                              ContestManuallyControlledField.NAME,
                              ContestManuallyControlledField.CATEGORY))
                      .sourceCategories(Set.of("sourceCategory1", "sourceCategory2")).build();
    }

    public static Contest createContest(ContestStatus status, ContestType type) {
        final String uuid = UUID.randomUUID().toString();
        Contest contest = new Contest();
        contest.setName("Rozenberg / Wagenaar vs Pontjodikromo / Wassermann");
        contest.setStatus(status);
        contest.setType(type);
        contest.setKey(uuid);
        return contest;
    }

    public static Proposition createProposition(Contest contest) {
        Proposition proposition = new Proposition();
        final UUID propositionKey = UUID.randomUUID();
        proposition.setKey(propositionKey.toString());
        proposition.setContest(contest);
        proposition.setName("Football simple");
        proposition.setType("total");
        return proposition;
    }
}
