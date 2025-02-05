package com.kindredgroup.kps.pricingentity.webapp.messaging.consumer;

import java.util.List;
import java.util.stream.IntStream;

import com.kindredgroup.kps.internal.kafka.FeedDomainMetadata;
import com.kindredgroup.kps.metrics.MetricConstants;
import com.kindredgroup.kps.metrics.util.MetricsHelper;
import com.kindredgroup.kps.pricingentity.feeddomain.logging.FeedDomainLoggingAction;
import com.kindredgroup.kps.pricingentity.logging.KafkaLogger;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class KafkaAspect {

    private static final Tag DOMAIN_TAG = Tag.of("domain", "feed-domain");
    private final KafkaLogger kafkaLogger;

    public KafkaAspect(KafkaLogger kafkaLogger) {
        this.kafkaLogger = kafkaLogger;
    }

    private static FeedDomainMetadata extractMetadata(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        int index = IntStream.range(0, signature.getParameterTypes().length)
                             .filter(i -> FeedDomainMetadata.class.equals(signature.getParameterTypes()[i]))
                             .findFirst().orElseThrow(
                        () -> new IllegalArgumentException("Annotated method has no FeedDomainMetadata argument"));
        return (FeedDomainMetadata) joinPoint.getArgs()[index];
    }

    private static Object extractPayload(ProceedingJoinPoint joinPoint, MethodSignature signature) {
        int index = IntStream.range(0, signature.getParameterTypes().length)
                             .filter(i -> ConsumerRecord.class.equals(signature.getParameterTypes()[i]))
                             .findFirst().orElseThrow(
                        () -> new IllegalArgumentException("Annotated method has no ConsumerRecord argument"));
        return ((ConsumerRecord) joinPoint.getArgs()[index]).value();
    }

    @Around("@annotation(com.kindredgroup.kps.internal.kafka.KafkaTimed)")
    public Object around(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final FeedDomainMetadata metadata = extractMetadata(joinPoint, signature);
        final Object payload = extractPayload(joinPoint, signature);
        return Metrics.timer(MetricConstants.KAFKA_CONSUMER_METRIC_NAME,
                              List.of(Tag.of(MetricsHelper.TAG_NAME, metadata.messageType()),
                                      DOMAIN_TAG))
                      .record(() -> {
                          try {
                              kafkaLogger.info(metadata.messageType(), FeedDomainLoggingAction.RECEIVING_FEED_DOMAIN_MESSAGE,
                                      payload);
                              return joinPoint.proceed();
                          } catch (Throwable e) {
                              throw new RuntimeException(e);
                          }
                      });
    }

}
