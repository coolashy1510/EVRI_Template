package com.kindredgroup.kps.pricingentity.persistence.feeddomain.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.service.VariantService;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Proposition;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Variant;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.PropositionRepository;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.VariantRepository;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class VariantServiceImpl implements VariantService {
    private final VariantRepository variantRepository;
    private final ContestServiceImpl contestService;
    private final PropositionRepository propositionRepository;

    public VariantServiceImpl(VariantRepository variantRepository, ContestServiceImpl contestService,
                              PropositionRepository propositionRepository) {
        this.variantRepository = variantRepository;
        this.contestService = contestService;
        this.propositionRepository = propositionRepository;
    }

    /**
     * Converts variants from the given {@code variants} list to entities for the given {@code proposition}.
     *
     * @throws IllegalArgumentException when some variants already exist for the given {@code proposition}
     */
    static List<Variant> convert(List<com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Variant> variants,
                                 Proposition proposition) {
        return variants.stream().map(variant -> {
            Variant item = new Variant(variant.getVariantKey(), proposition, variant.getVariantType());
            item.setName(variant.getName());
            return item;
        }).toList();

    }

    // [n.shvinagir] The name field so far is the only field that can be updated for a variant. If changes in the future, the
    // method could be redesigned.
    private Variant update(Variant variant, String name) {
        variant.setName(name);
        return variantRepository.save(variant);
    }

    /**
     * Updates the existing variant entity if found by payload variantKey from the given {@code message}.
     * <p/>
     * As the message does not contain a variant type required by the entity class, will never attempt to create a new entity.
     *
     * @throws IllegalStateException    if contest or proposition from message payload not found
     * @throws IllegalArgumentException if variant by variant key, contest, and proposition from message payload not found
     */
    @Timed(value = MetricConstants.METHOD_CALLS_METRIC_NAME, extraTags = {MetricsHelper.TAG_ALIAS, "saveVariant"},
           histogram = true)
    public void save(VariantChanged variantChanged) {
        final String contestKey = variantChanged.contestKey();
        final String propositionKey = variantChanged.propositionKey();
        final Optional<Contest> contest = contestService.findByKey(contestKey);
        if (contest.isEmpty()) {
            throw new IllegalStateException(
                    MessageFormat.format("Cannot persist the variant {0} of proposition {1}: contest={2} not found",
                            variantChanged.variantKey(), propositionKey, contestKey));
        }
        final Optional<Proposition> proposition = propositionRepository.findByContestAndKey(contest.get(), propositionKey);
        if (proposition.isEmpty()) {
            throw new IllegalStateException(
                    MessageFormat.format("Cannot persist the variant {0}: proposition {1} of contest={2} not found",
                            variantChanged.variantKey(), propositionKey, contestKey));
        }
        final Optional<Variant> variant = variantRepository.findByPropositionAndKey(proposition.get(),
                variantChanged.variantKey());
        if (variant.isEmpty()) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Variant {0} for proposition={1} and contest={2} does not exist",
                            variantChanged.variantKey(),
                            propositionKey, contestKey));
        }
        update(variant.get(), variantChanged.name());
    }
}
