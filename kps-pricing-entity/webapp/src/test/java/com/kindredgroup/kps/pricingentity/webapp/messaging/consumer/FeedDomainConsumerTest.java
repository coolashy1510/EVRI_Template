package com.kindredgroup.kps.pricingentity.webapp.messaging.consumer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.kindredgroup.kps.internal.kafka.FeedDomainMetadata;
import com.kindredgroup.kps.internal.kafka.PricingMetadata;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.service.FeedDomainEventService;
import com.kindredgroup.kps.pricingentity.logging.KafkaLogger;
import com.kindredgroup.kps.pricingentity.persistence.feeddomain.model.Contest;
import io.confluent.parallelconsumer.ParallelStreamProcessor;
import io.confluent.parallelconsumer.PollContext;
import io.confluent.parallelconsumer.RecordContext;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FeedDomainConsumerTest {
    private static final String MESSAGE_KEY = "kafkaKey";
    private static final String MESSAGE_TYPE = "SomethingChangedType";
    private static final String MSG_TYPE = "some message_type";
    private static final String CORRELATION_ID = " some correlation_id";
    private static final String MAJOR_VERSION = "1";
    private final OpenTelemetry openTelemetry = OpenTelemetry.noop();
    private final Tracer tracer = openTelemetry.getTracer("test-tracer");
    FeedDomainConsumer feedDomainConsumer;
    @Mock KafkaLogger kafkaLogger;
    @Mock private FeedDomainProcessor feedDomainProcessor;
    @Mock private ParallelStreamProcessor<String, Object> streamProcessor;
    @Captor
    private ArgumentCaptor<Function<PollContext<String, Object>, List<ProducerRecord<String, Object>>>> userFunctionCaptor;
    @Mock
    private Contest contest;

    @BeforeEach
    void setUp() {
        feedDomainConsumer = new FeedDomainConsumer(kafkaLogger, streamProcessor, feedDomainProcessor, openTelemetry, tracer);
        ReflectionTestUtils.setField(feedDomainConsumer, "OUTPUT_TOPIC", "some output topic");
    }

    @Test
    public void consumeFeedDomainMessage_ok() {

        feedDomainConsumer.consumeFeedDomainMessage();
        verify(streamProcessor, only()).pollAndProduceMany(userFunctionCaptor.capture(), any());

        PollContext<String, Object> pollContext = mock(PollContext.class);
        RecordContext<String, Object> recordContext = mock(RecordContext.class);
        when(pollContext.getSingleRecord()).thenReturn(recordContext);
        ConsumerRecord<String, Object> consumerRecord = new ConsumerRecord<>("some topic", 0, 0, MESSAGE_KEY, contest);
        consumerRecord.headers().add("majorVersion", "1".getBytes());
        Proposition resultPayload = mock(Proposition.class);
        PricingMetadata resultMetadata = new PricingMetadata(
                new FeedDomainMetadata(MESSAGE_TYPE, MSG_TYPE, MAJOR_VERSION, CORRELATION_ID), null);

        when(feedDomainProcessor.processIncomingMessage(any(), eq(consumerRecord))).thenReturn(
                Optional.of(new FeedDomainEventService.Result(resultPayload, resultMetadata)));
        when(recordContext.getConsumerRecord()).thenReturn(consumerRecord);
        final List<ProducerRecord<String, Object>> results = userFunctionCaptor.getValue().apply(pollContext);
        assertEquals(1, results.size());
        final ProducerRecord<String, Object> result = results.get(0);
        assertEquals(MESSAGE_KEY, result.key());
        assertEquals(resultPayload, result.value());
        final Header[] resultHeaders = result.headers().toArray();
        assertTrue(Arrays.stream(resultHeaders).anyMatch(
                item -> item.key().equals("correlationId") && Arrays.equals(item.value(), CORRELATION_ID.getBytes())));
        assertTrue(Arrays.stream(resultHeaders).anyMatch(
                item -> item.key().equals("majorVersion") && Arrays.equals(item.value(), MAJOR_VERSION.getBytes())));
        assertTrue(Arrays.stream(resultHeaders).anyMatch(
                item -> item.key().equals("Message-Type") && Arrays.equals(item.value(), MSG_TYPE.getBytes())));
        assertTrue(Arrays.stream(resultHeaders).anyMatch(
                item -> item.key().equals("messageType") && Arrays.equals(item.value(), MESSAGE_TYPE.getBytes())));
        assertTrue(Arrays.stream(resultHeaders).anyMatch(item -> item.key().equals("producer") &&
                Arrays.equals(item.value(), "parallel-pricing-entity-service".getBytes())));
    }

    @Test
    public void consumeFeedDomainMessage_noResult_ok() {

        feedDomainConsumer.consumeFeedDomainMessage();
        verify(streamProcessor, only()).pollAndProduceMany(userFunctionCaptor.capture(), any());

        PollContext<String, Object> pollContext = mock(PollContext.class);
        RecordContext<String, Object> recordContext = mock(RecordContext.class);
        when(pollContext.getSingleRecord()).thenReturn(recordContext);
        ConsumerRecord<String, Object> consumerRecord = new ConsumerRecord<>("some topic", 0, 0, MESSAGE_KEY, null);

        when(recordContext.getConsumerRecord()).thenReturn(consumerRecord);
        assertTrue(userFunctionCaptor.getValue().apply(pollContext).isEmpty());
        verifyNoInteractions(feedDomainProcessor);
    }


}
