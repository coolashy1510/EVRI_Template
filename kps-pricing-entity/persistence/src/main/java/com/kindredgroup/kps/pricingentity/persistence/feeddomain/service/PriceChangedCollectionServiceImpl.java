package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import com.kindredgroup.commons.logging.logger.KpsLogger;
import com.kindredgroup.commons.logging.logger.KpsLoggerFactory;
import com.kindredgroup.kps.internal.api.pricingdomain.OutcomePricesChanged;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.feeddomain.service.PriceChangedCollectionService;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.*;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ContestRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional
@Slf4j
public class PriceChangedCollectionServiceImpl implements PriceChangedCollectionService {
    private final ContestServiceImpl contestService;
    private final ContestRepository contestRepository;
    private final KpsLogger kpsLogger = KpsLoggerFactory.getLogger(log);

    public PriceChangedCollectionServiceImpl(ContestServiceImpl contestService,
                                             ContestRepository contestRepository) {
        this.contestService = contestService;
        this.contestRepository = contestRepository;
    }

    @Override
    @Transactional
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "savePriceChangedCollection"},
           histogram = true)
    public void savePriceChangedCollection(PriceChangedCollection changedPrices) {
        final String contestKey = changedPrices.contestKey();
        final Optional<Contest> contest = contestService.findByKey(contestKey);
        if (contest.isEmpty()) {
            throw new IllegalStateException(
                    MessageFormat.format("Cannot persist the Price Changed Collection: contest={0} not found", contestKey));
        }
        Contest persistedContest = contest.get();
        AtomicBoolean newPricesToSave = new AtomicBoolean(false);
        changedPrices.pricesChanged()
                     .forEach(outcomePricesChanged ->
                             persistedContest.getPropositions().stream()
                                             .filter(persistedProposition ->
                                                     persistedProposition.getKey().equals(outcomePricesChanged.propositionKey()))
                                             .findFirst()
                                             .ifPresent(proposition ->
                                                     checkOptionAndVariants(outcomePricesChanged, proposition, newPricesToSave)));
        if(newPricesToSave.get()) {
            try {
                contestRepository.save(persistedContest);
            } catch (Exception e) {
                kpsLogger.error(MessageFormat.format("Error saving prices for contest: {0}", persistedContest.getKey()));
            }
        }
    }

    private static void checkOptionAndVariants(OutcomePricesChanged outcomePricesChanged, Proposition proposition, AtomicBoolean newPricesToSave) {
        outcomePricesChanged.prices()
                            .forEach(outcomePrice ->
                                    proposition.getPrices().stream()
                                               .filter(price ->
                                                       price.getOption().getKey().equals(outcomePrice.optionKey()) && price.getVariant().getKey().equals(outcomePrice.variantKey()))
                                               .findFirst()
                                               .ifPresentOrElse(persistedPrice -> {
                                                               persistedPrice.setPrice(outcomePrice.price());
                                                               newPricesToSave.set(true);
                                                       },
                                                       () -> addNewPrice(proposition, newPricesToSave, outcomePrice))
                            );
    }

    private static void addNewPrice(Proposition proposition, AtomicBoolean newPricesToSave, com.kindredgroup.kps.internal.api.pricingdomain.Price outcomePrice) {
        Optional<Option> option = proposition.getOptions().stream()
                                             .filter(propositionOption ->
                                                     propositionOption.getKey().equals(outcomePrice.optionKey()))
                                             .findFirst();
        Optional<Variant> variant = proposition.getVariants().stream()
                                               .filter(propositionVariant ->
                                                       propositionVariant.getKey().equals(outcomePrice.variantKey()))
                                               .findFirst();
        if (option.isPresent() && variant.isPresent()) {
            proposition.getPrices().add(new Price(option.get(), variant.get(), outcomePrice.price()));
            newPricesToSave.set(true);
        }
    }

}

