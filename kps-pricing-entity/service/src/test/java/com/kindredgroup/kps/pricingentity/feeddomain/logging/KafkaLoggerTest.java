package com.kindredgroup.kps.pricingentity.feeddomain.logging;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kindredgroup.commons.logging.common.CustomField;
import com.kindredgroup.commons.logging.common.LoggingReason;
import com.kindredgroup.commons.logging.logger.KpsLogger;
import com.kindredgroup.commons.logging.logger.KpsLoggerFactory;
import com.kindredgroup.kps.internal.api.pricingdomain.Contest;
import com.kindredgroup.kps.pricingentity.feeddomain.domain.v1.Proposition;
import com.kindredgroup.kps.pricingentity.logging.KafkaLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static com.kindredgroup.commons.logging.common.CommonFields.PAYLOAD;
import static com.kindredgroup.commons.logging.common.KafkaFields.MESSAGE_TYPE;
import static com.kindredgroup.kps.pricingentity.logging.KafkaLoggingMarker.NOT_SET;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaLoggerTest {
    private final static String MESSAGE_TYPE_VALUE = "message type";
    private final static String CORELLATION_ID = "correlation id";
    private static final String SERIALIZED_PAYLOAD = "serialized payload";
    @Captor
    ArgumentCaptor<Map<CustomField, Object>> parametersCaptor;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private KpsLogger kpsLogger;
    private KafkaLogger kafkaLogger = new KafkaLogger();
    @Mock
    private LoggingReason reasonText;

    private AutoCloseable closeable;
    private MockedStatic<KpsLoggerFactory> mockedFactory;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mockedFactory = mockStatic(KpsLoggerFactory.class);
        mockedFactory.when(() -> KpsLoggerFactory.getLogger(any(Logger.class))).thenReturn(kpsLogger);
    }

    @AfterEach
    void terminate() throws Exception {
        closeable.close();
        mockedFactory.close();
    }

    @Test
    void info_ok() throws JsonProcessingException {
        Contest payload = mock(Contest.class);
        when(mapper.writeValueAsString(payload)).thenReturn(SERIALIZED_PAYLOAD);
        kafkaLogger.info(MESSAGE_TYPE_VALUE, FeedDomainLoggingAction.RECEIVING_FEED_DOMAIN_CONTEST, payload);
        verify(kpsLogger, only()).info(eq(FeedDomainLoggingAction.RECEIVING_FEED_DOMAIN_CONTEST), parametersCaptor.capture());
        final Map<CustomField, Object> actual = parametersCaptor.getValue();
        assertAll(
                () -> assertEquals(MESSAGE_TYPE_VALUE, actual.get(MESSAGE_TYPE)),
                () -> assertEquals(payload, actual.get(PAYLOAD))
        );
    }

    @Test
    void info_dimensionsNotSet_ok() {
        kafkaLogger.info(null, FeedDomainLoggingAction.RECEIVING_FEED_DOMAIN_CONTEST, null);
        verify(kpsLogger, only()).info(eq(FeedDomainLoggingAction.RECEIVING_FEED_DOMAIN_CONTEST), parametersCaptor.capture());
        final Map<CustomField, Object> actual = parametersCaptor.getValue();
        assertAll(
                () -> assertEquals(NOT_SET, actual.get(MESSAGE_TYPE)),
                () -> assertFalse(actual.containsKey(PAYLOAD))
        );
    }

    @Test
    void error_ok() throws JsonProcessingException {
        Proposition payload = mock(Proposition.class);
        Throwable throwable = mock(Throwable.class);
        kafkaLogger.error(MESSAGE_TYPE_VALUE, FeedDomainLoggingAction.RECEIVING_FEED_DOMAIN_PROPOSITION,
                reasonText, payload, throwable);
        verify(kpsLogger, only()).error(eq(FeedDomainLoggingAction.RECEIVING_FEED_DOMAIN_PROPOSITION), eq(reasonText),
                parametersCaptor.capture(), eq(throwable));
        final Map<CustomField, Object> actual = parametersCaptor.getValue();
        assertAll(
                () -> assertEquals(MESSAGE_TYPE_VALUE, actual.get(MESSAGE_TYPE)),
                () -> assertEquals(payload, actual.get(PAYLOAD))
        );
    }

    @Test
    void error_dimensionsNotSet_ok() {
        Throwable throwable = mock(Throwable.class);
        kafkaLogger.error(FeedDomainLoggingAction.RECEIVING_FEED_DOMAIN_CONTEST, reasonText, throwable);
        verify(kpsLogger, only()).error(eq(FeedDomainLoggingAction.RECEIVING_FEED_DOMAIN_CONTEST), eq(reasonText),
                parametersCaptor.capture(), eq(throwable));
        final Map<CustomField, Object> actual = parametersCaptor.getValue();
        assertAll(
                () -> assertEquals(NOT_SET, actual.get(MESSAGE_TYPE)),
                () -> assertFalse(actual.containsKey(PAYLOAD))
        );
    }
}
