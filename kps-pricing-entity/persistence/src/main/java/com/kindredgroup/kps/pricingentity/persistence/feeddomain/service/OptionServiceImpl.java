package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.internal.utils.EntityUtils;
import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.service.OptionService;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Option;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.OptionEntity;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.OptionRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.PropositionRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OptionServiceImpl implements OptionService {
    private final OptionRepository optionRepository;
    private final ContestServiceImpl contestService;
    private final PropositionRepository propositionRepository;

    public OptionServiceImpl(OptionRepository optionRepository, ContestServiceImpl contestService,
                             PropositionRepository propositionRepository) {
        this.optionRepository = optionRepository;
        this.contestService = contestService;
        this.propositionRepository = propositionRepository;
    }

    /**
     * Converts options from the given {@code options} list to entities for the given {@code proposition}.
     *
     * @throws IllegalArgumentException when some options already exist for the given {@code proposition}
     */
    static List<Option> convert(List<com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Option> options,
                                Proposition proposition) {
        return options.stream().map(option -> {
            Option item = new Option(option.getOptionKey(), proposition, option.getOptionType());
            // Added support to store the key for optionType such as Participant
            item.setOptionEntities(
                    option.getEntities() != null ?
                            option.getEntities().stream().map(entity -> new OptionEntity(item, entity.key(), entity.entityType()))
                                  .toList() : Collections.emptyList());
            item.setName(option.getName());
            return item;
        }).toList();

    }

    /**
     * Converts options from the given {@code options} list to entities for the given {@code proposition}.
     *
     * @throws IllegalArgumentException when some options already exist for the given {@code proposition}
     */
    public static List<Option> convertV2(List<com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.OptionV2> options,
                                  Proposition proposition) {
        return options.stream().map(option -> {
            Option item = new Option(option.getOptionKey(), proposition, option.getOptionType());
            item.setOptionEntities(EntityUtils.toEntities(option.getArguments()).stream()
                                              .map(entity -> new OptionEntity(item, entity.key(), entity.entityType())).toList());
            item.setName(option.getName());
            return item;
        }).toList();

    }

    // [n.shvinagir] The name field so far is the only field that can be updated for a option. If changes in the future, the
    // method could be redesigned.
    private Option update(Option option, String name) {
        option.setName(name);
        return optionRepository.save(option);

    }

    /**
     * Updates the existing option entity if found by payload optionKey from the given {@code message}.
     * <p/>
     * As the message does not contain a option type required by the entity class, will never attempt to create a new entity.
     *
     * @throws IllegalStateException    if contest or proposition from message payload not found
     * @throws IllegalArgumentException if option by option key, contest, and proposition from message payload not found
     */
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "saveOption"},
           histogram = true)
    public void save(OptionChanged optionChanged) {
        final String contestKey = optionChanged.contestKey();
        final String propositionKey = optionChanged.propositionKey();

        final Optional<Proposition> proposition = propositionRepository.findByContestAndKey(
                contestService.findByKey(contestKey).orElseThrow(() -> new IllegalStateException(
                        MessageFormat.format("Cannot persist the option {0} of proposition {1}: contest={2} not found",
                                optionChanged.optionKey(), propositionKey, contestKey))), propositionKey);

        final Optional<Option> option = optionRepository.findByPropositionAndKey(
                proposition.orElseThrow(() -> new IllegalStateException(
                        MessageFormat.format("Cannot persist the option {0}: proposition {1} of contest={2} not found",
                                optionChanged.optionKey(), propositionKey, contestKey))), optionChanged.optionKey());

        update(option.orElseThrow(() -> new IllegalArgumentException(
                MessageFormat.format("Option {0} for proposition={1} and contest={2} does not exist", optionChanged.optionKey(),
                        propositionKey, contestKey))), optionChanged.name());
    }
}
