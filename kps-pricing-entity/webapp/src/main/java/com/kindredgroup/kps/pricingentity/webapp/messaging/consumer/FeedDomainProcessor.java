package com.kindredgroup.kps.pricingentity.webapp.messaging.consumer;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kindredgroup.kps.internal.api.pricingdomain.Contest;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.internal.kafka.FeedDomainMetadata;
import com.kindredgroup.kps.internal.kafka.KafkaTimed;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2;
import com.kindredgroup.kps.pricingentity.feeddomain.logging.FeedDomainLoggingAction;
import com.kindredgroup.kps.pricingentity.feeddomain.service.FeedDomainEventService;
import com.kindredgroup.kps.pricingentity.logging.KafkaLogger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FeedDomainProcessor {

    private static final Logger log = LoggerFactory.getLogger(FeedDomainProcessor.class);
    private final KafkaLogger kafkaLogger;
    private final FeedDomainEventService feedDomainEventService;
    private final MessageRoutingProperties messageRoutingProperties;

    public FeedDomainProcessor(KafkaLogger kafkaLogger, FeedDomainEventService feedDomainEventService,
                               MessageRoutingProperties messageRoutingProperties) {
        this.kafkaLogger = kafkaLogger;
        this.feedDomainEventService = feedDomainEventService;
        this.messageRoutingProperties = messageRoutingProperties;
        if (this.messageRoutingProperties == null || this.messageRoutingProperties.routing() == null) {
            log.warn("No Kafka message routing configured");
        } else {
            log.info("Kafka message routing configured: {}", formatRoutingConfig());
        }
    }

    private String formatRoutingConfig() {
        return this.messageRoutingProperties.routing().entrySet().stream()
                                            .map(entry -> MessageFormat.format("{0}:[majorVersion={1}]",
                                                    entry.getKey(), entry.getValue().majorVersion()))
                                            .collect(Collectors.joining(","));
    }

    @KafkaTimed
    public Optional<FeedDomainEventService.Result> processIncomingMessage(FeedDomainMetadata kafkaMetadata,
                                                                          ConsumerRecord<String, Object> consumerRecord) {
        try {
            final Object payload = consumerRecord.value();

            return Optional.ofNullable(messageRoutingProperties.routing().get(kafkaMetadata.messageType()))
                           .flatMap(routingItem -> {
                               if (routingItem.majorVersion().equals(kafkaMetadata.majorVersion())) {
                                   return switch (payload) {
                                       case Contest value -> feedDomainEventService.processContestEvent(value, kafkaMetadata);
                                       case Proposition value ->
                                               feedDomainEventService.processPropositionEvent(value, kafkaMetadata);
                                       case PropositionV2 value ->
                                               feedDomainEventService.processPropositionV2Event(value, kafkaMetadata);
                                       case OptionChanged value -> feedDomainEventService.processOptionChangedEvent(value);
                                       case VariantChanged value -> feedDomainEventService.processVariantChangedEvent(value);
                                       case OutcomeResult value -> feedDomainEventService.processOutcomeResultEvent(value);
                                       case PriceChangedCollection value -> isProviderValid(value) ?
                                               feedDomainEventService.processPriceChangeCollection(value, kafkaMetadata) :
                                               Optional.empty();
                                       case PropositionChanged value ->
                                               feedDomainEventService.processPropositionChangedEvent(value);
                                       default -> Optional.empty();
                                   };
                               } else {
                                   return Optional.empty();
                               }

                           });

        } catch (Exception e) {
            kafkaLogger.error(FeedDomainLoggingAction.ERROR_RECEIVING_FEED_DOMAIN_MESSAGE, e::getMessage, e);
            return Optional.empty();
        }
    }

    private boolean isProviderValid(PriceChangedCollection payload) {
        return !payload.provider().equals(FeedProvider.QUANT);
    }

}
