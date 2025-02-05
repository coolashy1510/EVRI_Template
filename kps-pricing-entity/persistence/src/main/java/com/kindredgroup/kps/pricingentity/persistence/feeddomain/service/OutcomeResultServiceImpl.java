package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.internal.utils.QuantTypeUtils;
import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Fraction;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeFraction;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;
import com.kindredgroup.kps.pricingentity.feeddomain.service.OutcomeResultService;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Outcome;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.mapper.ArgumentMapper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.mapper.EntityMapper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.OutcomeRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class OutcomeResultServiceImpl implements OutcomeResultService {

    private final PropositionServiceImpl propositionService;
    private final OutcomeRepository outcomeRepository;
    private final EntityMapper entityMapper = EntityMapper.INSTANCE;
    private final ArgumentMapper argumentMapper = ArgumentMapper.INSTANCE;

    public OutcomeResultServiceImpl(PropositionServiceImpl propositionService, OutcomeRepository outcomeRepository) {
        this.propositionService = propositionService;
        this.outcomeRepository = outcomeRepository;
    }

    private static @NotNull Optional<Variant> findFirstVariantByKey(Proposition persistedProposition, String variantKey) {
        return persistedProposition.getVariants().stream().filter(variant -> variant.getKey().equals(variantKey))
                                   .findFirst();
    }

    private static @NotNull Optional<Option> findFirstOptionByKey(Proposition persistedProposition, String optionKey) {
        return persistedProposition.getOptions().stream().filter(option -> option.getKey().equals(optionKey)).findFirst();
    }


    @Override
    @Transactional
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "OutcomeResultService.saveOutcomeResult"}, histogram = true)
    public void save(OutcomeResult outcomeResult) {
        final String contestKey = outcomeResult.getContestKey();
        final Optional<Proposition> proposition = propositionService.get(contestKey, outcomeResult.getPropositionKey());
        if (proposition.isEmpty()) {
            throw new IllegalStateException(
                    MessageFormat.format("Cannot persist the Outcome Result: contest={0} not found", contestKey));
        }
        Proposition persistedProposition = proposition.get();
        List<Outcome> outcomeList = createValidOutcomes(persistedProposition, outcomeResult.getOutcomeFractions());
        matchPersistedOutcomes(persistedProposition, outcomeList);
        outcomeRepository.saveAll(outcomeList);
    }

    private List<Outcome> createValidOutcomes(Proposition persistedProposition, List<OutcomeFraction> outcomeFractions) {
        List<Outcome> outcomeList = new ArrayList<>();
        outcomeFractions.forEach(outcomeFraction -> {
            String optionKey = outcomeFraction.optionKey();
            String variantKey = outcomeFraction.variantKey();
            Optional<Option> persistedOption = findFirstOptionByKey(persistedProposition, optionKey);
            Optional<Variant> persistedVariant = findFirstVariantByKey(persistedProposition, variantKey);
            if (persistedOption.isPresent() && persistedVariant.isPresent()) {
                outcomeList.add(
                        new Outcome(persistedOption.get(), persistedVariant.get(), outcomeFraction.refundFraction().numerator(),
                                outcomeFraction.refundFraction().denominator(), outcomeFraction.winFraction().numerator(),
                                outcomeFraction.winFraction().denominator()));
            }
        });
        return outcomeList;
    }

    private void matchPersistedOutcomes(Proposition persistedProposition, List<Outcome> outcomeList) {
        List<Outcome> persistedOutcomeList = outcomeRepository.findByProposition(persistedProposition);
        for (Outcome persistedOutcome : persistedOutcomeList) {
            Optional<Outcome> matchingOutcome = outcomeList.parallelStream().filter(outcome ->
                    outcome.getPropositionId().equals(persistedOutcome.getPropositionId()) &&
                            outcome.getOptionId().equals(persistedOutcome.getOptionId()) &&
                            outcome.getVariantId().equals(persistedOutcome.getVariantId())).findFirst();
            if (matchingOutcome.isPresent()) {
                persistedOutcome.setRefundDenominator(matchingOutcome.get().getRefundDenominator());
                persistedOutcome.setRefundNumerator(matchingOutcome.get().getRefundNumerator());
                persistedOutcome.setWinDenominator(matchingOutcome.get().getWinDenominator());
                persistedOutcome.setWinNumerator(matchingOutcome.get().getWinNumerator());
                outcomeList.remove(matchingOutcome.get());
                outcomeList.add(persistedOutcome);
            }

        }
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME,
           extraTags = {MetricsHelper.TAG_ALIAS, "OutcomeResultService.getOutcomeResult"}, histogram = true)
    public List<OutcomeResult> getOutcomeResult(String contestKey) {
        List<Outcome> outcomesList = outcomeRepository.findByContestKey(contestKey);
        if (outcomesList.isEmpty()) {
            throw new IllegalStateException(MessageFormat.format("Cannot get any Outcome for contest={0}", contestKey));
        }
        List<OutcomeResult> outcomeResults = new ArrayList<>();
        for (Outcome outcome : outcomesList) {
            String propositionKey = outcome.getProposition().getKey();
            outcomeResults.stream().filter(outcomeResult -> outcomeResult.getPropositionKey().equals(propositionKey))
                          .findFirst().ifPresentOrElse(optionalOutcome -> {
                              OutcomeFraction outcomeFraction = createOutcomeFraction(outcome);
                              if (outcomeFraction != null) { optionalOutcome.getOutcomeFractions().add(outcomeFraction); }
                          }, () -> {
                              OutcomeFraction outcomeFraction = createOutcomeFraction(outcome);
                              if (outcomeFraction != null) {
                                  OutcomeResult outcomeResult = new OutcomeResult(contestKey, propositionKey);
                                  outcomeResult.getOutcomeFractions().add(outcomeFraction);
                                  outcomeResults.add(outcomeResult);
                              }
                          });
        }
        return outcomeResults;

    }

    private OutcomeFraction createOutcomeFraction(Outcome outcome) {
        var propositionArguments = outcome.getProposition().getPlaceholders().stream()
                                          .map(argumentMapper::propositionPlaceholderToArgument).toList();

        return QuantTypeUtils.convertToMarketType(outcome.getProposition().getType(), outcome.getVariant().getKey(),
                                     outcome.getVariant().getType(), propositionArguments).flatMap(
                                     quantMarketType -> QuantTypeUtils.convertToContractType(outcome.getOption().getType(),
                                                                              outcome.getOption().getKey(),
                                                                              QuantTypeUtils.convertToPropositionType(quantMarketType.className().getValue()),
                                                                              outcome.getOption().getOptionEntities().stream().map(entityMapper::toEntity).toList())
                                                                      .map(quantContractType -> new OutcomeFraction(outcome.getOption().getKey(),
                                                                              quantMarketType, outcome.getVariant().getKey(),
                                                                              quantContractType,
                                                                              new Fraction(outcome.getRefundNumerator(),
                                                                                      outcome.getRefundDenominator()),
                                                                              new Fraction(outcome.getWinNumerator(),
                                                                                      outcome.getWinDenominator()))))
                             .orElse(null);
    }

}
