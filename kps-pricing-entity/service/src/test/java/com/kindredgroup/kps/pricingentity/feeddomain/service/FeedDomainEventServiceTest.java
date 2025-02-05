package com.kindredgroup.kps.pricingentity.feeddomain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.kindredgroup.kps.internal.api.pricingdomain.Contest;
import com.kindredgroup.kps.internal.api.pricingdomain.EnrichedMapping;
import com.kindredgroup.kps.internal.api.pricingdomain.EnrichedPrices;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollection;
import com.kindredgroup.kps.internal.api.pricingdomain.PriceChangedCollectionEnriched;
import com.kindredgroup.kps.internal.api.pricingdomain.PropositionEnriched;
import com.kindredgroup.kps.internal.kafka.FeedDomainMetadata;
import com.kindredgroup.kps.internal.kafka.MessageType;
import com.kindredgroup.kps.internal.kafka.PricingMetadata;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.ContestType;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.OptionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.PropositionChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.VariantChanged;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v2.PropositionV2;
import com.kindredgroup.kps.pricingentity.pricingdomain.service.PriceChangedCollectionEnrichedService;
import com.kindredgroup.kps.pricingentity.pricingdomain.service.PropositionEnrichedService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.core.task.TaskExecutor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class FeedDomainEventServiceTest {

    public static final ContestType CONTEST_TYPE = ContestType.FOOTBALL;
    protected static final String CORRELATION_ID = "correlation-id";
    protected static final String MAJOR_VERSION = "majorVersion";
    protected static final String MESSAGE_TYPE_KEBAB = "Message-Type";
    protected static final String MESSAGE_TYPE = "messageType";
    private static final String CONTEST_KEY = "some key";
    private final ContestService contestService = mock(ContestService.class);
    private final PropositionService propositionService = mock(PropositionService.class);
    private final VariantService variantService = mock(VariantService.class);
    private final OptionService optionService = mock(OptionService.class);
    private final PriceChangedCollectionService priceChangedCollectionService = mock(PriceChangedCollectionService.class);
    private final PriceChangedCollectionEnrichedService priceChangedCollectionEnrichedService = mock(
            PriceChangedCollectionEnrichedService.class);
    private final OutcomeResultService outcomeResultService = mock(OutcomeResultService.class);
    private final PropositionEnrichedService propositionEnrichedService = mock(PropositionEnrichedService.class);

    private final TaskExecutor taskExecutor = mock(TaskExecutor.class);
    private final FeedDomainEventService service = new FeedDomainEventService(contestService, propositionService,
            optionService,
            variantService, priceChangedCollectionService,
            priceChangedCollectionEnrichedService, outcomeResultService, propositionEnrichedService, taskExecutor);
    @Captor
    private ArgumentCaptor<Runnable> taskCaptor;
    private AutoCloseable closeable;

    private static FeedDomainMetadata getFeedDomainMetadata() {
        return new FeedDomainMetadata(MESSAGE_TYPE, MESSAGE_TYPE_KEBAB, MAJOR_VERSION, CORRELATION_ID);
    }

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void terminate() throws Exception {
        closeable.close();
    }

    @Test
    void processContestEvent() {
        Contest contest = mock(Contest.class);
        when(contest.contestType()).thenReturn(ContestType.FOOTBALL.getValue());
        service.processContestEvent(contest, getFeedDomainMetadata());
        verify(contestService, times(1)).save(contest);
        verifyNoMoreInteractions(contestService);
    }

    @Test
    void processESoccerContestEvent() {
        Contest contest = mock(Contest.class);
        when(contest.contestType()).thenReturn(ContestType.ESOCCER.getValue());
        service.processContestEvent(contest, getFeedDomainMetadata());
        verifyNoInteractions(contestService);
    }

    @Test
    void processPropositionEvent() {
        Proposition proposition = mock(Proposition.class);
        when(proposition.contestKey()).thenReturn(CONTEST_KEY);
        when(contestService.getContestType(proposition.contestKey())).thenReturn(Optional.of(CONTEST_TYPE));
        PropositionEnriched propositionEnriched = PropositionEnriched.builder()
                                                                     .contestKey("contestKey")
                                                                     .propositionKey("propositionKey")
                                                                     .enrichedMappings(List.of(EnrichedMapping.builder().build()))
                                                                     .build();
        when(propositionEnrichedService.enriched(proposition)).thenReturn(propositionEnriched);
        final Optional<FeedDomainEventService.Result> result = service.processPropositionEvent(proposition,
                getFeedDomainMetadata());
        assertTrue(result.isPresent());
        final PricingMetadata actualMetadata = result.get().metadata();
        assertEquals(CORRELATION_ID, actualMetadata.correlationId());
        assertEquals(MessageType.PropositionEnriched.name(), actualMetadata.messageType());
        assertEquals(MESSAGE_TYPE_KEBAB, actualMetadata.message_type());
        assertEquals(MAJOR_VERSION, actualMetadata.majorVersion());
        assertEquals(CONTEST_TYPE.getValue(), actualMetadata.contestType().orElseThrow());
        verify(propositionService, times(1)).save(proposition);
        verifyNoMoreInteractions(propositionService);
    }

    @Test
    void processPropositionEvent_contestTypeNotFound_exceptionThrown() {
        Proposition proposition = mock(Proposition.class);
        when(proposition.contestKey()).thenReturn(CONTEST_KEY);
        when(contestService.getContestType(proposition.contestKey())).thenReturn(Optional.empty());
        FeedDomainMetadata feedDomainMetadata = getFeedDomainMetadata();
        assertThrows(IllegalStateException.class,
                () -> service.processPropositionEvent(proposition, feedDomainMetadata));
        verify(propositionService, times(1)).save(proposition);
        verifyNoMoreInteractions(propositionService);
    }

    @Test
    void processPropositionChangedEvent() {
        PropositionChanged propositionChanged = mock(PropositionChanged.class);
        service.processPropositionChangedEvent(propositionChanged);
        verify(propositionService, times(1)).update(propositionChanged);
        verifyNoMoreInteractions(propositionService);
    }

    @Test
    void processVariantChangedEvent() {
        VariantChanged variantChanged = mock(VariantChanged.class);
        service.processVariantChangedEvent(variantChanged);
        verify(variantService, times(1)).save(variantChanged);
        verifyNoMoreInteractions(variantService);
    }

    @Test
    void processPriceChangedCollectionEventNotProduced() {
        PriceChangedCollection priceChangedCollection = mock(PriceChangedCollection.class);
        PriceChangedCollectionEnriched priceChangedCollectionEnriched = new PriceChangedCollectionEnriched("testContestKey",
                List.of());
        when(priceChangedCollectionEnrichedService.enriched(priceChangedCollection)).thenReturn(priceChangedCollectionEnriched);
        Optional<FeedDomainEventService.Result> message = service.processPriceChangeCollection(priceChangedCollection,
                getFeedDomainMetadata());
        assertFalse(message.isPresent());
        verify(priceChangedCollectionEnrichedService, times(1)).enriched(priceChangedCollection);
        verify(taskExecutor, only()).execute(taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(priceChangedCollectionService, only()).savePriceChangedCollection(priceChangedCollection);
    }

    @Test
    void processPriceChangedCollectionEvent() {
        PriceChangedCollection priceChangedCollection = mock(PriceChangedCollection.class);
        List<EnrichedPrices> outcomePricesChangedEnriched = new ArrayList<>(
                Collections.singleton(new EnrichedPrices("propositionKey", null)));
        PriceChangedCollectionEnriched priceChangedCollectionEnriched = PriceChangedCollectionEnriched.builder()
                                                                                                      .contestKey("contestKey")
                                                                                                      .pricesChanged(
                                                                                                              outcomePricesChangedEnriched)
                                                                                                      .build();
        when(priceChangedCollectionEnrichedService.enriched(priceChangedCollection)).thenReturn(priceChangedCollectionEnriched);
        when(contestService.getContestType(any())).thenReturn(Optional.of(CONTEST_TYPE));
        Optional<FeedDomainEventService.Result> message = service.processPriceChangeCollection(priceChangedCollection,
                getFeedDomainMetadata());
        assertInstanceOf(PriceChangedCollectionEnriched.class, message.orElseThrow().payload());
        assertEquals(((PriceChangedCollectionEnriched) message.get().payload()).contestKey(),
                priceChangedCollectionEnriched.contestKey());
        final PricingMetadata actualMetadata = message.get().metadata();
        assertEquals(CORRELATION_ID, actualMetadata.correlationId());
        assertEquals(MessageType.PriceChangedCollectionEnriched.name(), actualMetadata.messageType());
        assertEquals(MESSAGE_TYPE_KEBAB, actualMetadata.message_type());
        assertEquals(MAJOR_VERSION, actualMetadata.majorVersion());
        assertEquals(CONTEST_TYPE.getValue(), actualMetadata.contestType().orElseThrow());
        verify(priceChangedCollectionEnrichedService, times(1)).enriched(priceChangedCollection);
        verify(taskExecutor, only()).execute(taskCaptor.capture());
        taskCaptor.getValue().run();
        verify(priceChangedCollectionService, only()).savePriceChangedCollection(priceChangedCollection);
    }

    @Test
    void processPriceChangedCollectionEvent_wrongBody_exceptionThrown() {
        Object priceChangedCollection = mock(Contest.class);
        assertThrows(ClassCastException.class,
                () -> service.processPriceChangeCollection(priceChangedCollection, getFeedDomainMetadata()));
        verifyNoInteractions(priceChangedCollectionEnrichedService, taskExecutor, priceChangedCollectionService);
    }

    @Test
    void processOptionChangedEvent() {
        OptionChanged optionChanged = mock(OptionChanged.class);
        service.processOptionChangedEvent(optionChanged);
        verify(optionService, times(1)).save(optionChanged);
        verifyNoMoreInteractions(optionService);
    }

    @Test
    void processPropositionV2Event_ok() {
        final PropositionV2 proposition = mock(PropositionV2.class);
        when(proposition.contestKey()).thenReturn(CONTEST_KEY);

        final PropositionEnriched propositionEnriched = mock(PropositionEnriched.class);
        when(propositionEnrichedService.enriched(proposition)).thenReturn(propositionEnriched);
        when(propositionEnriched.enrichedMappings()).thenReturn(List.of(mock(EnrichedMapping.class)));
        when(contestService.getContestType(CONTEST_KEY)).thenReturn(Optional.of(CONTEST_TYPE));
        FeedDomainEventService.Result result = service.processPropositionV2Event(proposition, getFeedDomainMetadata())
                                                      .orElseThrow();
        verify(propositionService, times(1)).save(proposition);
        assertEquals(propositionEnriched, result.payload());
        assertEquals(CONTEST_TYPE.getValue(), result.metadata().contestType().orElseThrow());
        assertEquals(MESSAGE_TYPE_KEBAB, result.metadata().message_type());
        assertEquals(MAJOR_VERSION, result.metadata().majorVersion());
        assertEquals(CORRELATION_ID, result.metadata().correlationId());
        assertEquals(MessageType.PropositionEnriched.name(), result.metadata().messageType());

    }

    @Test
    void processPropositionV2Event_noEnrichedMappings_ok() {
        final PropositionV2 proposition = mock(PropositionV2.class);
        final PropositionEnriched propositionEnriched = mock(PropositionEnriched.class);
        when(propositionEnrichedService.enriched(proposition)).thenReturn(propositionEnriched);
        when(propositionEnriched.enrichedMappings()).thenReturn(List.of());

        assertTrue(service.processPropositionV2Event(proposition, getFeedDomainMetadata()).isEmpty());
        verify(propositionService, times(1)).save(proposition);
        verify(propositionEnrichedService, times(1)).enriched(proposition);
        verifyNoInteractions(contestService);

    }
}
