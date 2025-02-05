package com.kindredgroup.kps.pricingentity.webapp.messaging.consumer;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.kindredgroup.kps.internal.api.pricingdomain.Contest;
import com.kindredgroup.kps.internal.api.pricingdomain.FeedProvider;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.internal.kafka.FeedDomainMetadata;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OutcomeResult;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2;
import com.kindredgroup.kps.pricingentity.feeddomain.service.FeedDomainEventService;
import com.kindredgroup.kps.pricingentity.logging.KafkaLogger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class FeedDomainProcessorTest {

    public static final String MAJOR_VERSION = "42";
    public static final RoutingItem ROUTING_ITEM_V_1 = new RoutingItem(MAJOR_VERSION, null);
    private final ConsumerRecord<String, Object> message = mock(ConsumerRecord.class);
    private final FeedDomainEventService.Result result = mock(FeedDomainEventService.Result.class);
    private final FeedDomainMetadata metadata = mock(FeedDomainMetadata.class);
    @Mock KafkaLogger kafkaLogger;
    private FeedDomainProcessor feedDomainProcessor;
    @Mock
    private FeedDomainEventService feedDomainEventService;
    @Mock
    private MessageRoutingProperties messageRoutingProperties;

    private static Stream<Arguments> provideNonQuantProviders() {
        return Arrays.stream(FeedProvider.values()).filter(item -> !item.equals(FeedProvider.QUANT)).map(Arguments::of);
    }

    @BeforeEach
    void setUp() {
        feedDomainProcessor = new FeedDomainProcessor(kafkaLogger, feedDomainEventService, messageRoutingProperties);
    }

    @Test
    void processIncomingMessage_exceptionThrown_ok() {
        when(metadata.messageType()).thenReturn("Contest");
        when(messageRoutingProperties.routing()).thenThrow(new RuntimeException());
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verify(messageRoutingProperties, times(3)).routing(); // constructor
        verifyNoInteractions(feedDomainEventService);
    }

    @Test
    void processIncomingMessage_noMatchingRouting_ok() {
        when(metadata.messageType()).thenReturn("Contest");
        when(metadata.majorVersion()).thenReturn(MAJOR_VERSION);
        when(message.value()).thenReturn(mock(Contest.class));
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("Proposition", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verifyNoInteractions(feedDomainEventService);
    }

    @Test
    void processIncomingMessage_noMatchingMajorVersion_ok() {
        when(metadata.messageType()).thenReturn("Contest");
        when(metadata.majorVersion()).thenReturn("43");
        when(message.value()).thenReturn(mock(Contest.class));
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("Contest", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verifyNoInteractions(feedDomainEventService);
    }


    @Test
    void processIncomingMessage_contest_ok() {
        when(metadata.messageType()).thenReturn("Contest");
        when(metadata.majorVersion()).thenReturn(MAJOR_VERSION);
        when(message.value()).thenReturn(mock(Contest.class));
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("Contest", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));
        final Contest payload = (Contest) message.value();
        when(feedDomainEventService.processContestEvent(payload, metadata)).thenReturn(
                Optional.ofNullable(result));
        final FeedDomainEventService.Result actualResult = feedDomainProcessor.processIncomingMessage(metadata, message)
                                                                              .orElseThrow();

        assertEquals(result, actualResult);
        when(feedDomainEventService.processContestEvent(payload, metadata)).thenReturn(Optional.empty());
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verify(feedDomainEventService, times(2)).processContestEvent(payload, metadata);
        verifyNoMoreInteractions(feedDomainEventService);
    }

    @Test
    void processIncomingMessage_proposition_ok() {
        when(metadata.messageType()).thenReturn("Proposition");
        when(metadata.majorVersion()).thenReturn(MAJOR_VERSION);
        when(message.value()).thenReturn(mock(Proposition.class));
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("Proposition", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));
        final Proposition payload = (Proposition) message.value();
        when(feedDomainEventService.processPropositionEvent(payload, metadata)).thenReturn(
                Optional.ofNullable(result));
        final FeedDomainEventService.Result actualResult = feedDomainProcessor.processIncomingMessage(metadata, message)
                                                                              .orElseThrow();

        assertEquals(result, actualResult);
        when(feedDomainEventService.processPropositionEvent(payload, metadata)).thenReturn(
                Optional.empty());
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verify(feedDomainEventService, times(2)).processPropositionEvent(payload, metadata);
        verifyNoMoreInteractions(feedDomainEventService);
    }

    @Test
    void processIncomingMessage_propositionV2_ok() {
        when(metadata.messageType()).thenReturn("Proposition");
        when(metadata.majorVersion()).thenReturn(MAJOR_VERSION);
        when(message.value()).thenReturn(mock(PropositionV2.class));
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("Proposition", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));
        final PropositionV2 payload = (PropositionV2) message.value();
        when(feedDomainEventService.processPropositionV2Event(payload, metadata)).thenReturn(
                Optional.ofNullable(result));
        final FeedDomainEventService.Result actualResult = feedDomainProcessor.processIncomingMessage(metadata, message)
                                                                              .orElseThrow();

        assertEquals(result, actualResult);
        when(feedDomainEventService.processPropositionV2Event(payload, metadata)).thenReturn(
                Optional.empty());
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verify(feedDomainEventService, times(2)).processPropositionV2Event(payload, metadata);
        verifyNoMoreInteractions(feedDomainEventService);
    }

    @Test
    void processIncomingMessage_optionChanged_ok() {
        when(metadata.messageType()).thenReturn("OptionChanged");
        when(metadata.majorVersion()).thenReturn(MAJOR_VERSION);
        when(message.value()).thenReturn(mock(OptionChanged.class));
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("OptionChanged", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));
        final OptionChanged payload = (OptionChanged) message.value();
        when(feedDomainEventService.processOptionChangedEvent(payload)).thenReturn(Optional.ofNullable(result));
        final FeedDomainEventService.Result actualResult = feedDomainProcessor.processIncomingMessage(metadata, message)
                                                                              .orElseThrow();
        assertEquals(result, actualResult);
        when(feedDomainEventService.processOptionChangedEvent(payload)).thenReturn(Optional.empty());
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verify(feedDomainEventService, times(2)).processOptionChangedEvent(payload);
        verifyNoMoreInteractions(feedDomainEventService);
    }

    @Test
    void processIncomingMessage_variantChanged_ok() {
        when(metadata.messageType()).thenReturn("VariantChanged");
        when(metadata.majorVersion()).thenReturn(MAJOR_VERSION);
        when(message.value()).thenReturn(mock(VariantChanged.class));
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("VariantChanged", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));
        final VariantChanged payload = (VariantChanged) message.value();
        when(feedDomainEventService.processVariantChangedEvent(payload)).thenReturn(Optional.ofNullable(result));
        final FeedDomainEventService.Result actualResult = feedDomainProcessor.processIncomingMessage(metadata, message)
                                                                              .orElseThrow();
        assertEquals(result, actualResult);
        when(feedDomainEventService.processVariantChangedEvent(payload)).thenReturn(Optional.empty());
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verify(feedDomainEventService, times(2)).processVariantChangedEvent(payload);
        verifyNoMoreInteractions(feedDomainEventService);
    }

    @ParameterizedTest
    @MethodSource(value = {"provideNonQuantProviders"})
    void processIncomingMessage_priceChangedCollection_ok(FeedProvider provider) {
        when(metadata.messageType()).thenReturn("PriceChangedCollection");
        when(metadata.majorVersion()).thenReturn(MAJOR_VERSION);
        final PriceChangedCollection payload = mock(PriceChangedCollection.class);
        when(payload.provider()).thenReturn(provider);
        when(message.value()).thenReturn(payload);
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("PriceChangedCollection", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));
        when(feedDomainEventService.processPriceChangeCollection(message.value(), metadata)).thenReturn(
                Optional.ofNullable(result));

        final FeedDomainEventService.Result actualResult = feedDomainProcessor.processIncomingMessage(metadata, message)
                                                                              .orElseThrow();
        assertEquals(result, actualResult);
        when(feedDomainEventService.processPriceChangeCollection(message.value(), metadata)).thenReturn(
                Optional.empty());
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verify(feedDomainEventService, times(2)).processPriceChangeCollection(message.value(), metadata);
        verifyNoMoreInteractions(feedDomainEventService);
    }

    @Test
    void processIncomingMessage_priceChangedCollectionQuant_ok() {
        when(metadata.messageType()).thenReturn("PriceChangedCollection");
        when(metadata.majorVersion()).thenReturn(MAJOR_VERSION);
        final PriceChangedCollection payload = mock(PriceChangedCollection.class);
        when(payload.provider()).thenReturn(FeedProvider.QUANT);
        when(message.value()).thenReturn(payload);
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("PriceChangedCollection", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));

        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verify(feedDomainEventService, never()).processPriceChangeCollection(message.value(), metadata);
        verifyNoMoreInteractions(feedDomainEventService);
    }

    @Test
    void processIncomingMessage_propositionChanged_ok() {
        when(metadata.messageType()).thenReturn("PropositionChanged");
        when(metadata.majorVersion()).thenReturn(MAJOR_VERSION);
        when(message.value()).thenReturn(mock(PropositionChanged.class));
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("PropositionChanged", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));
        final PropositionChanged payload = (PropositionChanged) message.value();
        when(feedDomainEventService.processPropositionChangedEvent(payload)).thenReturn(Optional.ofNullable(result));
        final FeedDomainEventService.Result actualResult = feedDomainProcessor.processIncomingMessage(metadata, message)
                                                                              .orElseThrow();
        assertEquals(result, actualResult);
        when(feedDomainEventService.processPropositionChangedEvent(payload)).thenReturn(Optional.empty());
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verify(feedDomainEventService, times(2)).processPropositionChangedEvent(payload);
        verifyNoMoreInteractions(feedDomainEventService);
    }

    @Test
    void processIncomingMessage_outcomeResult_ok() {
        when(metadata.messageType()).thenReturn("OutcomeResult");
        when(metadata.majorVersion()).thenReturn(MAJOR_VERSION);
        when(message.value()).thenReturn(mock(OutcomeResult.class));
        when(messageRoutingProperties.routing()).thenReturn(
                Map.of("OutcomeResult", ROUTING_ITEM_V_1, "FakeMessageType", ROUTING_ITEM_V_1));
        final OutcomeResult payload = (OutcomeResult) message.value();
        when(feedDomainEventService.processOutcomeResultEvent(payload)).thenReturn(Optional.ofNullable(result));
        final FeedDomainEventService.Result actualResult = feedDomainProcessor.processIncomingMessage(metadata, message)
                                                                              .orElseThrow();
        assertEquals(result, actualResult);
        when(feedDomainEventService.processOutcomeResultEvent(payload)).thenReturn(Optional.empty());
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message).isEmpty());
        verify(feedDomainEventService, times(2)).processOutcomeResultEvent(payload);
        verifyNoMoreInteractions(feedDomainEventService);
    }

    @Test
    void processIncomingMessage_unknown_ok() {
        when(metadata.messageType()).thenReturn("Unknown");
        assertTrue(feedDomainProcessor.processIncomingMessage(metadata, message)
                                      .isEmpty());
        verifyNoInteractions(feedDomainEventService);

    }
}
