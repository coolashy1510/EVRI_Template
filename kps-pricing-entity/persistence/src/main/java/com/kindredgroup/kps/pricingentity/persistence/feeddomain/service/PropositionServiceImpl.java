package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.kindredgroup.commons.logging.logger.KpsLogger;
import com.kindredgroup.commons.logging.logger.KpsLoggerFactory;
import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2;
import com.kindredgroup.kps.pricingentity.feeddomain.service.PropositionService;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.PropositionPlaceholder;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.mapper.ArgumentMapper;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.PropositionRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class PropositionServiceImpl implements PropositionService {
    private final ContestServiceImpl contestService;
    private final PropositionRepository propositionRepository;
    private final KpsLogger kpsLogger = KpsLoggerFactory.getLogger(log);
    private final ArgumentMapper argumentMapper = ArgumentMapper.INSTANCE;

    public PropositionServiceImpl(ContestServiceImpl contestService, PropositionRepository propositionRepository) {
        this.contestService = contestService;
        this.propositionRepository = propositionRepository;
    }

    private static PropositionPlaceholder createPlaceholder(String name, String value, Proposition proposition) {
        final PropositionPlaceholder placeholder = new PropositionPlaceholder();
        placeholder.setProposition(proposition);
        placeholder.setName(name);
        placeholder.setValue(value);
        return placeholder;
    }

    public Optional<Proposition> get(Contest contest, String key) {
        return propositionRepository.findByContestAndKey(contest, key);
    }

    public Optional<Proposition> get(String contestKey, String propositionKey) {
        return propositionRepository.findByContestKeyAndKey(contestKey, propositionKey);
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "saveProposition"},
           histogram = true)
    @Transactional
    @Deprecated(since = "Proposition message major version = 2")
    public void save(com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition payload) {
        final String contestKey = payload.contestKey();
        final String propositionKey = payload.propositionKey();
        final Optional<Contest> contest = contestService.findByKey(contestKey);
        if (contest.isEmpty()) {
            throw new IllegalStateException(
                    MessageFormat.format("Cannot persist the proposition: contest={0} not found", contestKey));
        }

        contest.get().getPropositions().stream()
               .filter(item -> item.getKey().equals(propositionKey))
               .findAny()
               .ifPresentOrElse(item -> updateProposition(item, payload), () -> {
                   Proposition proposition = new Proposition();
                   proposition.setKey(propositionKey);
                   proposition.setName(payload.name());
                   proposition.setType(payload.propositionType());
                   proposition.setContest(contest.get());

                   final List<Option> options = OptionServiceImpl.convert(payload.options(), proposition);
                   final List<Variant> variants = VariantServiceImpl.convert(payload.variants(), proposition);

                   proposition.setPlaceholders(payload.placeholders().entrySet().stream()
                                                      .map(entry -> createPlaceholder(entry.getKey(),
                                                              entry.getValue(),
                                                              proposition)
                                                      ).toList());
                   proposition.setOptions(options);
                   proposition.setVariants(variants);
                   propositionRepository.save(proposition);
               });
    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "updateProposition"},
           histogram = true)
    public void update(PropositionChanged payload) {
        final String contestKey = payload.contestKey();
        final String propositionKey = payload.propositionKey();
        final Optional<Contest> contest = contestService.findByKey(contestKey);
        if (contest.isEmpty()) {
            throw new IllegalStateException(
                    MessageFormat.format("Cannot update the proposition: contest={0} not found", contestKey));
        }

        contest.get().getPropositions().stream()
               .filter(item -> item.getKey().equals(propositionKey))
               .findAny()
               .ifPresentOrElse(item -> {
                   if (Objects.nonNull(payload.name())) {
                       item.setName(payload.name());
                       propositionRepository.save(item);
                   }
               }, () -> {
                   throw new IllegalStateException(
                           MessageFormat.format("Cannot update the proposition: proposition={0} not found",
                                   propositionKey));
               });

    }

    @Override
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "savePropositionV2"},
           histogram = true)
    @Transactional
    public void save(PropositionV2 payload) {
        final String contestKey = payload.contestKey();
        final String propositionKey = payload.propositionKey();
        final Optional<Contest> contest = contestService.findByKey(contestKey);
        if (contest.isEmpty()) {
            throw new IllegalStateException(
                    MessageFormat.format("Cannot persist the proposition: contest={0} not found", contestKey));
        }

        contest.get().getPropositions().stream()
               .filter(item -> item.getKey().equals(propositionKey))
               .findAny()
               .ifPresentOrElse(item -> updateProposition(item, payload), () -> {
                   Proposition proposition = new Proposition();
                   proposition.setKey(propositionKey);
                   proposition.setName(payload.name());
                   proposition.setType(payload.propositionType());
                   proposition.setContest(contest.get());

                   final List<Option> options = OptionServiceImpl.convertV2(payload.options(), proposition);
                   final List<Variant> variants = VariantServiceImpl.convert(payload.variants(), proposition);

                   proposition.setPlaceholders(payload.arguments().stream()
                                                      .map(arg -> argumentMapper.argumentToPropositionPlaceholder(
                                                              arg, proposition)).toList());
                   proposition.setOptions(options);
                   proposition.setVariants(variants);
                   propositionRepository.save(proposition);
               });
    }

    @Deprecated(since = "Proposition message major version = 2")
    private void updateProposition(Proposition proposition,
                                   com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition payload) {
        boolean isUpdated = updateFields(proposition, payload);
        if (isUpdated) {
            kpsLogger.info("The proposition with id: " + proposition.getId() + " was updated");
            propositionRepository.save(proposition);
        }
    }

    private void updateProposition(Proposition proposition,
                                   com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2 payload) {
        boolean isUpdated = updateFields(proposition, payload);
        if (isUpdated) {
            kpsLogger.info("The proposition with id: " + proposition.getId() + " was updated");
            propositionRepository.save(proposition);
        }
    }

    /**
     * It returns true if the new variants, options, or placeholders are found in the payload and updates the
     * existing proposition with the new data.
     *
     * @param proposition Existing proposition record retrieved from the table.
     * @param payload     Proposition record received from the "RECEIVING_FEED_DOMAIN_PROPOSITION" event.
     */
    private boolean updateFields(Proposition proposition,
                                 com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition payload) {

        List<Option> newOptions = OptionServiceImpl.convert(payload.options(), proposition);
        List<Variant> newVariants = VariantServiceImpl.convert(payload.variants(), proposition);
        List<PropositionPlaceholder> newPlaceholders = payload.placeholders().entrySet().stream()
                                                              .map(entry -> createPlaceholder(entry.getKey(), entry.getValue(),
                                                                      proposition)).toList();

        return updateFields(proposition, newOptions, newVariants, newPlaceholders);
    }

    /**
     * It returns true if the new variants, options, or placeholders are found in the payload and updates the
     * existing proposition with the new data.
     *
     * @param proposition Existing proposition record retrieved from the table.
     * @param payload     Proposition record received from the "RECEIVING_FEED_DOMAIN_PROPOSITION" event.
     */
    private boolean updateFields(Proposition proposition,
                                 com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2 payload) {


        List<Option> newOptions = OptionServiceImpl.convertV2(payload.options(), proposition);
        List<Variant> newVariants = VariantServiceImpl.convert(payload.variants(), proposition);
        List<PropositionPlaceholder> newPlaceholders = payload.arguments().stream()
                                                              .map(arg -> argumentMapper.argumentToPropositionPlaceholder(
                                                                      arg, proposition)).toList();

        return updateFields(proposition, newOptions, newVariants, newPlaceholders);
    }

    /**
     * It determines whether a new item in a collection should be saved based on its key value not existing in the original
     * collection.
     */
    private <T> Predicate<T> isNewItem(List<T> existingList, Function<T, Object> keyExtractor) {
        return newItem -> existingList.stream()
                                      .noneMatch(existingItem -> Objects.equals(keyExtractor.apply(existingItem),
                                              keyExtractor.apply(newItem)));
    }

    private boolean updateFields(Proposition proposition,
                                 List<Option> options, List<Variant> variants, List<PropositionPlaceholder> placeholders) {

        List<Option> existingOptions = proposition.getOptions();
        List<Variant> existingVariants = proposition.getVariants();
        List<PropositionPlaceholder> existingPlaceholders = proposition.getPlaceholders();

        List<Option> newOptionsToSave = options.stream()
                                               .filter(isNewItem(existingOptions, Option::getKey))
                                               .toList();

        List<Variant> newVariantsToSave = variants.stream()
                                                  .filter(isNewItem(existingVariants, Variant::getKey))
                                                  .toList();

        List<PropositionPlaceholder> newPlaceholdersToSave = placeholders.stream()
                                                                         .filter(isNewItem(
                                                                                 existingPlaceholders,
                                                                                 PropositionPlaceholder::getName)).toList();

        proposition.getOptions().addAll(newOptionsToSave);
        proposition.getVariants().addAll(newVariantsToSave);
        proposition.getPlaceholders().addAll(newPlaceholdersToSave);

        return !newOptionsToSave.isEmpty() || !newVariantsToSave.isEmpty() || !newPlaceholdersToSave.isEmpty();
    }

}
