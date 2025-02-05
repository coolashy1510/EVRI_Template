package com.kindredgroup.kps.pricingentity.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.kindredgroup.commons.logging.common.CustomField;
import com.kindredgroup.commons.logging.common.LoggingReason;
import com.kindredgroup.commons.logging.logger.KpsLogger;
import com.kindredgroup.commons.logging.logger.KpsLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.kindredgroup.commons.logging.common.CommonFields.PAYLOAD;
import static com.kindredgroup.commons.logging.common.KafkaFields.MESSAGE_TYPE;
import static com.kindredgroup.kps.pricingentity.logging.KafkaLoggingMarker.NOT_SET;

@Component
public class KafkaLogger {

    public <T> void info(String messageType, LoggingActionImpl<T> action, T payload) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        KpsLogger kpsLogger = KpsLoggerFactory.getLogger(LoggerFactory.getLogger(callerClass));
        kpsLogger.info(action, getLoggingDimensions(messageType, payload));
    }

    private <T> void logError(String messageType, LoggingActionImpl<T> action, LoggingReason reason, T payload, Throwable e) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        KpsLogger kpsLogger = KpsLoggerFactory.getLogger(LoggerFactory.getLogger(callerClass));
        kpsLogger.error(action, reason, getLoggingDimensions(messageType, payload), e);
    }

    public <T> void error(LoggingActionImpl<T> action, LoggingReason reason, Throwable e) {
        this.logError(null, action, reason, null, e);
    }

    public <T> void error(LoggingActionImpl<T> action, LoggingReason reason, T payload, Throwable e) {
        this.error(null, action, reason, payload, e);
    }

    public <T> void error(String messageType, LoggingActionImpl<T> action, LoggingReason reason, T payload, Throwable e) {
        this.logError(messageType, action, reason, payload, e);
    }

    public <T> void debug(String format, Object... args) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        final Logger logger = LoggerFactory.getLogger(callerClass);
        logger.debug(format, args);
    }

    public <T> void error(String messageType, LoggingActionImpl<T> action, LoggingReason reason) {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        KpsLogger kpsLogger = KpsLoggerFactory.getLogger(LoggerFactory.getLogger(callerClass));
        kpsLogger.error(action, reason, Map.of());
    }

    private Map<CustomField, Object> getLoggingDimensions(final String messageType, final Object payload) {
        final Map<CustomField, Object> loggingDimensions = new HashMap<>();
        loggingDimensions.put(MESSAGE_TYPE, Objects.toString(messageType, NOT_SET));
        if (Objects.nonNull(payload)) {
            loggingDimensions.put(PAYLOAD, payload);
        }
        return loggingDimensions;
    }

}
