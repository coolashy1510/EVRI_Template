package com.kindredgroup.kps.pricingentity.feeddomain.service;

import java.text.MessageFormat;
import java.util.Optional;

import com.kindredgroup.kps.internal.api.pricingdomain.Contest;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollectionEnriched;
import com.kindredgroup.kps.internal.api.pricingdomain.PropositionEnriched;
import com.kindredgroup.kps.internal.kafka.FeedDomainMetadata;
import com.kindredgroup.kps.internal.kafka.MessageType;
import com.kindredgroup.kps.internal.kafka.PricingMetadata;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2;
import com.kindredgroup.kps.pricingentity.pricingdomain.service.PriceChangedCollectionEnrichedService;
import com.kindredgroup.kps.pricingentity.pricingdomain.service.PropositionEnrichedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class FeedDomainEventService {

    private final ContestService contestService;
    private final PropositionService propositionService;
    private final VariantService variantService;
    private final OptionService optionService;
    private final PriceChangedCollectionService priceChangedCollectionService;
    private final PriceChangedCollectionEnrichedService priceChangedCollectionEnrichedService;
    private final OutcomeResultService outcomeResultService;
    private final PropositionEnrichedService propositionEnrichedService;
    private final TaskExecutor taskExecutor;

    public FeedDomainEventService(ContestService contestService, PropositionService propositionService,
                                  OptionService optionService,
                                  VariantService variantService, PriceChangedCollectionService priceChangedCollectionService,
                                  PriceChangedCollectionEnrichedService priceChangedCollectionEnrichedService,
                                  OutcomeResultService outcomeResultService,
                                  PropositionEnrichedService propositionEnrichedService,
                                  @Qualifier("pricingThreadPoolTaskExecutor") TaskExecutor taskExecutor) {
        this.contestService = contestService;
        this.propositionService = propositionService;
        this.variantService = variantService;
        this.optionService = optionService;
        this.priceChangedCollectionService = priceChangedCollectionService;
        this.priceChangedCollectionEnrichedService = priceChangedCollectionEnrichedService;
        this.outcomeResultService = outcomeResultService;
        this.propositionEnrichedService = propositionEnrichedService;
        this.taskExecutor = taskExecutor;
    }

    public Optional<Result> processContestEvent(final Contest value, FeedDomainMetadata kafkaMetadata) {
        if (!value.contestType().equals(ContestType.ESOCCER.getValue())) {
            contestService.save(value);
            //TODO:bertrand.coppe:2023-03-16: link contest to fixture and request the fixture
            //TODO:nikita.shvinagir:2023-02-21: log persistence result and modify the verifyLog() method in unit tests. So
            // far any
            // extra INFO logging within the consumer would break the test
            final var pricingMetadata = new PricingMetadata(kafkaMetadata, value.contestType());
            return Optional.of(new Result(value, pricingMetadata));
        }
        return Optional.empty();
    }

    @Deprecated(since = "Proposition message major version = 2")
    public Optional<Result> processPropositionEvent(final Proposition value, FeedDomainMetadata kafkaMetadata) {
        propositionService.save(value);
        var pricingMetadata = new PricingMetadata(
                new FeedDomainMetadata(MessageType.PropositionEnriched.name(), kafkaMetadata.message_type(),
                        kafkaMetadata.majorVersion(), kafkaMetadata.correlationId()),
                getContestType(value.contestKey()).getValue());
        PropositionEnriched propositionEnriched = propositionEnrichedService.enriched(value);
        if (!propositionEnriched.enrichedMappings().isEmpty()) {
            return Optional.of(new Result(propositionEnriched, pricingMetadata));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Result> processPropositionV2Event(final PropositionV2 value, FeedDomainMetadata kafkaMetadata) {
        propositionService.save(value);
        PropositionEnriched propositionEnriched = propositionEnrichedService.enriched(value);
        if (!propositionEnriched.enrichedMappings().isEmpty()) {
            var pricingMetadata = new PricingMetadata(
                    new FeedDomainMetadata(MessageType.PropositionEnriched.name(), kafkaMetadata.message_type(),
                            kafkaMetadata.majorVersion(), kafkaMetadata.correlationId()),
                    getContestType(value.contestKey()).getValue());
            return Optional.of(new Result(propositionEnriched, pricingMetadata));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Result> processPropositionChangedEvent(final PropositionChanged value) {
        propositionService.update(value);
        return Optional.empty();
    }

    public Optional<Result> processOptionChangedEvent(final OptionChanged value) {
        optionService.save(value);
        return Optional.empty();
    }

    public Optional<Result> processPriceChangeCollection(final Object priceChangedCollection,
                                                         FeedDomainMetadata kafkaMetadata) {
        PriceChangedCollection value = (PriceChangedCollection) priceChangedCollection;
            /* Saving prices is done in a separate thread as we don't going to use them as part of this process.
            The price from feed_domain will be the same as the one saved if there is no issue.
            If something fails while saving the data, we will log the error with the contestKey associated to this
            collection to investigate it.
            */
        taskExecutor.execute(() -> priceChangedCollectionService.savePriceChangedCollection(value));
        PriceChangedCollectionEnriched enriched = priceChangedCollectionEnrichedService.enriched(value);
        if (enriched != null && !CollectionUtils.isEmpty(enriched.pricesChanged())) {
            var pricingMetadata = new PricingMetadata(
                    new FeedDomainMetadata(MessageType.PriceChangedCollectionEnriched.name(), kafkaMetadata.message_type(),
                            kafkaMetadata.majorVersion(),
                            kafkaMetadata.correlationId()), getContestType(value.contestKey()).getValue());
            return Optional.of(new Result(enriched, pricingMetadata));
        }
        return Optional.empty();
    }

    public Optional<Result> processOutcomeResultEvent(final OutcomeResult value) {
        outcomeResultService.save(value);
        return Optional.empty();
    }

    public Optional<Result> processVariantChangedEvent(VariantChanged value) {
        variantService.save(value);
        return Optional.empty();
    }

    private ContestType getContestType(String contestKey) {
        return contestService.getContestType(contestKey).orElseThrow(() -> new IllegalStateException(
                MessageFormat.format("Cannot retrieve Contest Type: " + "contest={0} not found", contestKey)));
    }

    public record Result(Object payload, PricingMetadata metadata) {
    }
}
