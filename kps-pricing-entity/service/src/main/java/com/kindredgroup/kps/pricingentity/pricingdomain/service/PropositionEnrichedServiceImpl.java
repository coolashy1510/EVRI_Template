package com.kindredgroup.kps.pricingentity.pricingdomain.service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kindredgroup.commons.logging.logger.KpsLogger;
import com.kindredgroup.commons.logging.logger.KpsLoggerFactory;
import com.kindredgroup.kps.internal.api.pricingdomain.EnrichedMapping;
import com.kindredgroup.kps.internal.api.pricingdomain.Entity;
import com.kindredgroup.kps.internal.api.pricingdomain.PropositionEnriched;
import com.kindredgroup.kps.internal.api.pricingdomain.PropositionType;
import com.kindredgroup.kps.internal.api.pricingdomain.QuantContractType;
import com.kindredgroup.kps.internal.api.pricingdomain.QuantMarketType;
import com.kindredgroup.kps.internal.utils.QuantTypeUtils;
import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Option;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Variant;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.OptionV2;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
//TODO:nikita.shvinagir:2025-01-23: this class is very bad covered with tests. They check almost nothing. It is hard to test this
// class as it has many conversions in private method. Refactor and fix the test coverage. KPS-1585
public class PropositionEnrichedServiceImpl implements PropositionEnrichedService {

    private final KpsLogger kpsLogger = KpsLoggerFactory.getLogger(log);

    private static List<String> getOptionEntityKeys(Option option) {
        return Optional.ofNullable(option.getEntities()).orElse(Collections.emptyList()).stream().map(Entity::key)
                       .collect(Collectors.toList());

    }

    private static QuantContractType mapToQuantContractType(Option option, PropositionType propositionType,
                                                            List<String> optionEntityKeys) {
        return QuantTypeUtils.convertToContractTypeV1(option.getOptionType(), option.getOptionKey(), propositionType,
                optionEntityKeys).orElseThrow(() -> new IllegalStateException(MessageFormat.format(
                "Cannot convert the {0} option type to a Quant contract type! The {1} proposition type " +
                        "mapping is not implemented.", option.getOptionType(), propositionType)));
    }

    private static QuantMarketType mapToQuantMarketType(Proposition proposition, Variant variant) {
        return QuantTypeUtils.convertToMarketType(proposition.propositionType(), variant.getVariantKey(),
                variant.getVariantType(), proposition.placeholders()).orElseThrow(() -> new IllegalStateException(
                MessageFormat.format(
                        "Cannot convert the {0} proposition type to a Quant market type! The {1} variant " + "type " +
                                "mapping is not implemented.", proposition.propositionType(), variant.getVariantType())));
    }

    private static QuantContractType mapToQuantContractType(OptionV2 option, PropositionType propositionType) {
        return QuantTypeUtils.convertToContractTypeWithArguments(option.getOptionType(), option.getOptionKey(), propositionType,
                option.getArguments()
        ).orElseThrow(() -> new IllegalStateException(MessageFormat.format(
                "Cannot convert the {0} option type to a Quant contract type! The {1} proposition type " +
                        "mapping is not implemented.", option.getOptionType(), propositionType)));
    }

    private static QuantMarketType mapToQuantMarketType(PropositionV2 proposition, Variant variant) {
        return QuantTypeUtils.convertToMarketType(proposition.propositionType(), variant.getVariantKey(),
                variant.getVariantType(), proposition.arguments()).orElseThrow(() -> new IllegalStateException(
                MessageFormat.format(
                        "Cannot convert the {0} proposition type to a Quant market type! The {1} variant " + "type " +
                                "mapping is not implemented.", proposition.propositionType(), variant.getVariantType())));
    }

    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "PropositionEnrichedService.enriched"}, histogram = true)
    @Override
    public PropositionEnriched enriched(Proposition proposition) {
        List<EnrichedMapping> enrichedMappings = enrichedMappings(proposition);
        return PropositionEnriched.builder().contestKey(proposition.contestKey()).propositionKey(proposition.propositionKey())
                                  .enrichedMappings(enrichedMappings).build();
    }

    @Override
    public PropositionEnriched enriched(PropositionV2 proposition) {
        List<EnrichedMapping> enrichedMappings = enrichedMappings(proposition);
        return PropositionEnriched.builder().contestKey(proposition.contestKey()).propositionKey(proposition.propositionKey())
                                  .enrichedMappings(enrichedMappings).build();
    }

    private List<EnrichedMapping> enrichedMappings(PropositionV2 proposition) {
        return proposition.options().stream()
                          .flatMap(option -> proposition.variants().stream()
                                                        .flatMap(variant -> enrichedMapping(option, variant,
                                                                proposition).stream())).toList();
    }

    public List<EnrichedMapping> enrichedMappings(Proposition proposition) {
        return proposition.options().stream()
                          .flatMap(option -> proposition.variants().stream()
                                                        .map(variant -> enrichedMapping(option, variant, proposition)))
                          .filter(Optional::isPresent).map(Optional::get).toList();
    }

    private Optional<EnrichedMapping> enrichedMapping(Option option, Variant variant, Proposition proposition) {
        try {
            var quantMarketType = mapToQuantMarketType(proposition, variant);
            var propositionType =
                    QuantTypeUtils.convertToPropositionType(quantMarketType.className().value);
            var optionEntityKeys = getOptionEntityKeys(option);
            var quantContractType = mapToQuantContractType(option, propositionType, optionEntityKeys);
            return Optional.ofNullable(
                    EnrichedMapping.builder().optionKey(option.getOptionKey())
                                   .variantKey(variant.getVariantKey())
                                   .quantContractType(quantContractType)
                                   .quantMarketType(quantMarketType).build());
        } catch (Exception e) {
            kpsLogger.error(
                    MessageFormat.format("Could not enrich the option: `{0}` for proposition key `{1}`: ",
                            option, proposition.propositionKey()));
            return Optional.empty();
        }
    }

    private Optional<EnrichedMapping> enrichedMapping(OptionV2 option, Variant variant, PropositionV2 proposition) {
        try {
            var quantMarketType = mapToQuantMarketType(proposition, variant);
            var propositionType = QuantTypeUtils.convertToPropositionType(quantMarketType.className().value);
            var quantContractType = mapToQuantContractType(option, propositionType);
            return Optional.ofNullable(
                    EnrichedMapping.builder().optionKey(option.getOptionKey())
                                   .variantKey(variant.getVariantKey())
                                   .quantContractType(quantContractType)
                                   .quantMarketType(quantMarketType).build());
        } catch (Exception e) {
            kpsLogger.error(
                    MessageFormat.format("Could not enrich the option: `{0}` for proposition key `{1}`: ",
                            option, proposition.propositionKey()));
            return Optional.empty();
        }
    }

}
