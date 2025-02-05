package com.kindredgroup.kps.pricingentity.webapp.messaging.consumer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.kindredgroup.kps.internal.kafka.FeedDomainMetadata;
import com.kindredgroup.kps.internal.kafka.MetadataFields;
import com.kindredgroup.kps.internal.kafka.PricingMetadata;
import com.kindredgroup.kps.internal.utils.OpenTelemetryKafkaHeaderUtils;
import com.kindredgroup.kps.pricingentity.feeddomain.logging.FeedDomainLoggingAction;
import com.kindredgroup.kps.pricingentity.logging.KafkaLogger;
import io.confluent.parallelconsumer.ParallelStreamProcessor;
import io.confluent.parallelconsumer.PollContext;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapSetter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeedDomainConsumer {

    private static final String PRODUCER_NAME = "parallel-pricing-entity-service";

    private final KafkaLogger kafkaLogger;
    private final ParallelStreamProcessor<String, Object> streamProcessor;
    private final FeedDomainProcessor feedDomainProcessor;
    private final OpenTelemetry openTelemetry;
    private final Tracer tracer;
    private final TextMapGetter<Headers> getter = OpenTelemetryKafkaHeaderUtils.getter();
    private final TextMapSetter<Headers> setter = OpenTelemetryKafkaHeaderUtils.setter();
    @Value("${topic.env.prefix}.pricing.pricing_domain")
    private String OUTPUT_TOPIC;


    public FeedDomainConsumer(KafkaLogger kafkaLogger, ParallelStreamProcessor<String, Object> streamProcessor,
                              FeedDomainProcessor feedDomainProcessor, OpenTelemetry openTelemetry, Tracer tracer) {
        this.kafkaLogger = kafkaLogger;
        this.streamProcessor = streamProcessor;
        this.feedDomainProcessor = feedDomainProcessor;
        this.openTelemetry = openTelemetry;
        this.tracer = tracer;
    }

    @Bean
    protected void consumeFeedDomainMessage() {
        final Map<FeedDomainMetadata, Long> retriesCount = new ConcurrentHashMap<>();
        streamProcessor.pollAndProduceMany(pollContext -> processPollContext(pollContext, retriesCount),
                outMessage -> kafkaLogger.info(
                        FeedDomainMetadata.from(outMessage.getOut().headers()).messageType(),
                        FeedDomainLoggingAction.PRODUCING_PRICING_DOMAIN_MESSAGE, outMessage.getOut().value()));

    }

    private List<ProducerRecord<String, Object>> processPollContext(PollContext<String, Object> pollContext,
                                                                    Map<FeedDomainMetadata, Long> retriesCount) {
        ConsumerRecord<String, Object> consumerRecord = pollContext.getSingleRecord().getConsumerRecord();
        // [shvinagir] The CustomDeserializer.class will return null if the pollContext type is not supported. Ignore these
        // messages.
        if (consumerRecord.value() == null) {
            return List.of();
        }

        // Make the extracted from `traceparent` context current to propagate it downstream
        Context extractedContext = openTelemetry.getPropagators().getTextMapPropagator()
                                                .extract(Context.current(), consumerRecord.headers(), getter);
        FeedDomainMetadata kafkaMetadata = FeedDomainMetadata.from(consumerRecord.headers());
        Span span = tracer.spanBuilder("processFeedDomainMessage")
                          .setSpanKind(SpanKind.CONSUMER)
                          .setAttribute(MetadataFields.CORRELATION_ID, kafkaMetadata.correlationId())
                          .setParent(extractedContext)
                          .startSpan();
        try (var scope = extractedContext.with(span).makeCurrent()) {
            // TODO: nikita.shvinagir:2024-10-07: can we get 2 messages with the same kafkaMetadata?
            //  (Correlation ID is not unique in feed domain). Maybe worth having the traceparent as a key here?
            Long retryCount = retriesCount.compute(kafkaMetadata, (key, oldValue) -> oldValue == null ? 0L : oldValue + 1);
            if (retryCount < 1) {
                final var producerRecord = feedDomainProcessor.processIncomingMessage(kafkaMetadata, consumerRecord)
                                                              .map(result -> composeProducerRecord(consumerRecord.key(),
                                                                      result.payload(), result.metadata()));
                retriesCount.remove(kafkaMetadata);
                // Inject the trace context into the outgoing message's headers
                producerRecord.ifPresent(
                        record -> openTelemetry.getPropagators().getTextMapPropagator().inject(
                                Context.current(),
                                record.headers(),
                                setter
                        ));
                return producerRecord.stream().toList();
            }
            log.warn("Retry count {} exceeded max of {} for record {}", retryCount, 1, consumerRecord);
            retriesCount.remove(kafkaMetadata);
            return List.of();
        } catch (Throwable t) {
            span.setStatus(StatusCode.ERROR);
            return List.of();
        } finally {
            span.end();
        }

    }

    private ProducerRecord<String, Object> composeProducerRecord(String key, Object payload,
                                                                 PricingMetadata kafkaMetadata) {
        RecordHeaders headers = new RecordHeaders(kafkaMetadata.toCustomHeaders(PRODUCER_NAME).entrySet().stream()
                                                               .map(entry -> new RecordHeader(entry.getKey(),
                                                                       (byte[]) entry.getValue()))
                                                               .collect(Collectors.toUnmodifiableList()));
        return new ProducerRecord<>(OUTPUT_TOPIC, null, key, payload, headers);
    }

}
