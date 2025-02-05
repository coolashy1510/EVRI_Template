package com.kindredgroup.kps.pricingentity.persistence.pricingdomain.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.kindredgroup.kps.internal.api.pricingdomain.EnrichedPrice;
import com.kindredgroup.kps.internal.api.pricingdomain.EnrichedPrices;
import com.kindredgroup.kps.internal.api.pricingdomain.OutcomePricesChanged;
import com.kindredgroup.kps.internal.api.pricingdomain.Price;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollectionEnriched;
import com.kindredgroup.kps.internal.api.pricingdomain.QuantContractType;
import com.kindredgroup.kps.internal.api.pricingdomain.QuantMarketType;
import com.kindredgroup.kps.internal.utils.QuantTypeUtils;
import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.PropositionPlaceholder;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.mapper.ArgumentMapper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.mapper.EntityMapper;
import com.kindredgroup.kps.pricingentity.persistence.pricingdomain.repository.PriceChangedCollectionRepository;
import com.kindredgroup.kps.pricingentity.pricingdomain.service.PriceChangedCollectionEnrichedService;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Transactional
public class PriceChangedCollectionEnrichedServiceImpl implements PriceChangedCollectionEnrichedService {

    private final PriceChangedCollectionRepository priceChangedCollectionRepository;
    private final EntityMapper entityMapper = EntityMapper.INSTANCE;
    private final ArgumentMapper argumentMapper = ArgumentMapper.INSTANCE;

    public PriceChangedCollectionEnrichedServiceImpl(PriceChangedCollectionRepository priceChangedCollectionRepository) {
        this.priceChangedCollectionRepository = priceChangedCollectionRepository;
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "enriched"}, histogram = true)
    @Override
    public PriceChangedCollectionEnriched enriched(PriceChangedCollection priceChangedCollection) {
        List<Proposition> persistedPropositions = priceChangedCollectionRepository.findByKey(priceChangedCollection.contestKey());
        if (persistedPropositions != null && !persistedPropositions.isEmpty()) {
            PriceChangedCollectionEnriched.PriceChangedCollectionEnrichedBuilder builder =
                    PriceChangedCollectionEnriched.builder();
            builder.contestKey(priceChangedCollection.contestKey());
            builder.pricesChanged(getOutcomePricesChangedEnrichedList(priceChangedCollection, persistedPropositions));
            return builder.build();
        } else {
            log.info("Cannot update the Price Changed Collection: contest={} has 0 propositions",
                    priceChangedCollection.contestKey());
            return null;
        }
    }

    @NotNull
    private List<EnrichedPrices> getOutcomePricesChangedEnrichedList(PriceChangedCollection priceChangedCollection,
                                                                     List<Proposition> persistedPropositions) {
        List<EnrichedPrices> enrichedPricesList = new ArrayList<>();
        List<String> missingPropositionKeyList = new ArrayList<>();
        Multimap<String, Price> missingOptionOrVariantMap = ArrayListMultimap.create();
        priceChangedCollection.pricesChanged().forEach(
                processPricesChangedCollection(persistedPropositions, enrichedPricesList, missingPropositionKeyList,
                        missingOptionOrVariantMap));
        logWrongPropositions(priceChangedCollection, enrichedPricesList, missingPropositionKeyList, missingOptionOrVariantMap);
        return enrichedPricesList;
    }

    private void logWrongPropositions(PriceChangedCollection priceChangedCollection, List<EnrichedPrices> enrichedPricesList,
                                      List<String> missingPropositionKeyList, Multimap<String, Price> missingOptionOrVariantMap) {
        if (enrichedPricesList.isEmpty()) {
            log.info("Cannot update the Price Changed Collection: contest={}. Message received and DB have no common prices",
                    priceChangedCollection.contestKey());
        }
        if (!missingPropositionKeyList.isEmpty()) {
            log.info("Cannot update the Price Changed Collection: contest={}. The list of PropositionKey={} could not be found",
                    priceChangedCollection.contestKey(), Arrays.toString(missingPropositionKeyList.toArray()));
        }
        if (!missingOptionOrVariantMap.isEmpty()) {
            log.info(
                    "Cannot update the Price Changed Collection: contest={}. Pairs of optionKey/variantKey={} have inconsistent" +
                            " data",
                    priceChangedCollection.contestKey(), missingOptionOrVariantMap);
        }
    }

    @NotNull
    private Consumer<OutcomePricesChanged> processPricesChangedCollection(List<Proposition> persistedPropositions,
                                                                          List<EnrichedPrices> enrichedPrices,
                                                                          List<String> missingPropositionKeyList,
                                                                          Multimap<String, Price> missingOptionOrVariantMap) {
        return outcomePricesChanged ->
                persistedPropositions
                        .stream()
                        .filter(
                                proposition -> outcomePricesChanged.propositionKey().equals(proposition.getKey()))
                        .findFirst()
                        .ifPresentOrElse(
                                proposition -> processProposition(enrichedPrices, missingOptionOrVariantMap, outcomePricesChanged,
                                        proposition),
                                () -> missingPropositionKeyList.add(outcomePricesChanged.propositionKey()));
    }

    private void processProposition(List<EnrichedPrices> enrichedPricesList, Multimap<String, Price> missingOptionOrVariantMap,
                                    OutcomePricesChanged outcomePricesChanged,
                                    com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition proposition) {
        List<EnrichedPrice> priceEnrichedList = new ArrayList<>();
        outcomePricesChanged.prices().forEach(price -> {
            Optional<Variant> optionalVariant = proposition.getVariants().stream()
                                                           .filter(variant -> price.variantKey().equals(variant.getKey()))
                                                           .findFirst();
            Optional<Option> optionalOption = proposition.getOptions().stream()
                                                         .filter(option -> price.optionKey().equals(option.getKey())).findFirst();
            if (optionalOption.isPresent() && optionalVariant.isPresent()) {
                EnrichedPrice enrichedPrice = getEnrichedPriceByKeys(price.price(), proposition.getType(),
                        proposition.getPlaceholders(), optionalOption.get(), optionalVariant.get());
                if (enrichedPrice != null) {
                    priceEnrichedList.add(enrichedPrice);
                } else {
                    missingOptionOrVariantMap.put(outcomePricesChanged.propositionKey(), price);
                }
            } else {
                missingOptionOrVariantMap.put(outcomePricesChanged.propositionKey(), price);
            }
        });
        if (!priceEnrichedList.isEmpty()) {
            enrichedPricesList.add(new EnrichedPrices(proposition.getKey(), priceEnrichedList));
        }
    }

    private EnrichedPrice getEnrichedPriceByKeys(BigDecimal price, String propositionType,
                                                 List<PropositionPlaceholder> placeholders, Option option, Variant variant) {
        Optional<QuantMarketType> quantMarketType = QuantTypeUtils.convertToMarketType(propositionType,
                variant.getKey(),
                variant.getType(), placeholders.stream().map(argumentMapper::propositionPlaceholderToArgument).toList());
        if (quantMarketType.isPresent()) {
            Optional<QuantContractType> quantContractType = QuantTypeUtils.convertToContractType(option.getType(),
                    option.getKey(),
                    QuantTypeUtils.convertToPropositionType(quantMarketType.get().className().getValue()),
                    option.getOptionEntities().stream().map(entityMapper::toEntity).toList());
            return quantContractType.map(contractType -> EnrichedPrice.builder()
                                                                      .optionKey(option.getKey())
                                                                      .quantContractType(contractType)
                                                                      .variantKey(variant.getKey())
                                                                      .quantMarketType(quantMarketType.get())
                                                                      .price(price).build()).orElse(null);
        } else {
            return null;
        }
    }
}
