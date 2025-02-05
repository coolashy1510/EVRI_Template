package com.kindredgroup.kps.pricingentity.webapp.config;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.kindredgroup.kps.internal.kafka.CustomSerializer;
import com.kindredgroup.kps.pricingentity.webapp.messaging.consumer.CustomDeserializer;
import io.confluent.parallelconsumer.ParallelConsumerOptions;
import io.confluent.parallelconsumer.ParallelStreamProcessor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.kafka.KafkaClientMetrics;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.MessageHeaders;

import static io.confluent.parallelconsumer.ParallelConsumerOptions.ProcessingOrder.KEY;
import static pl.tlinkowski.unij.api.UniLists.of;

@Configuration
public class KafkaConfig implements EnvironmentAware {

    private final MeterRegistry meterRegistry;
    @Value("${topic.env.prefix}.feed.feed_domain")
    private String INPUT_TOPIC;
    @Value("${kafka.port}")
    private String KAFKA_PORT;
    @Value("${consumer.concurrency}")
    private Integer CONSUMER_CONCURRENCY;
    @Value("${message.buffer.size}")
    private Integer MESSAGE_BUFFER_SIZE;
    private Environment environment;

    public KafkaConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    private Consumer<String, Object> getKafkaConsumer() {
        List<String> bootstrapServers = List.of("kb001.ksp-cnt1-int.syd1.kc.thinkbig.local",
                "kb002.ksp-cnt1-int.syd1.kc.thinkbig.local", "kb003.ksp-cnt1-int.syd1.kc.thinkbig.local",
                "kb004.ksp-cnt1-int.syd1.kc.thinkbig.local", "kb005.ksp-cnt1-int.syd1.kc.thinkbig.local");
        final List<String> servers = environment.getProperty(
                org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, List.class,
                bootstrapServers);

        /*
          Current setup is 32 partitions with messages in them of 60KB size in average. Let's presume we want to limit the
          fetch per partition to 300 messages, the max.partition.fetch.bytes must be ~18_000KB.
          Therefore, the messageBufferSize should be calculated by the consumer options like loadFactor*32*300 = 19_200
         */
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                servers.stream().map(server -> String.join(":", server, KAFKA_PORT)).collect(
                        Collectors.joining(",")));
        properties.setProperty(
                org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                CustomDeserializer.class.getName());
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG,
                environment.getProperty(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG,
                        "confluent-pricing-entity-ivan"));
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG,
                environment.getProperty(org.apache.kafka.clients.consumer.ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "120000"));
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                environment.getProperty(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"));
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                environment.getProperty(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"));
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                environment.getProperty(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG,
                        "18432000")); // ~18_000KB
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,
                environment.getProperty(org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "30000"));
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG,
                environment.getProperty(org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "5000"));
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG,
                "io.opentelemetry.instrumentation.kafkaclients.v2_6.TracingConsumerInterceptor");
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "SCRAM-SHA-512");
        properties.put("sasl.jaas.config", environment.getProperty("sasl.jaas.config"));
        return new KafkaConsumer<>(properties);
    }

    private Producer<String, Object> getKafkaProducer() {
        List<String> bootstrapServers = List.of("kb001.ksp-cnt1-int.syd1.kc.thinkbig.local",
                "kb002.ksp-cnt1-int.syd1.kc.thinkbig.local", "kb003.ksp-cnt1-int.syd1.kc.thinkbig.local",
                "kb004.ksp-cnt1-int.syd1.kc.thinkbig.local", "kb005.ksp-cnt1-int.syd1.kc.thinkbig.local");
        final List<String> servers = environment.getProperty(
                org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, List.class,
                bootstrapServers);

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                servers.stream().map(server -> String.join(":", server, KAFKA_PORT)).collect(
                        Collectors.joining(",")));
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CustomSerializer.class.getName());
        properties.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        properties.put("security.protocol", "SASL_PLAINTEXT");
        properties.put("sasl.mechanism", "SCRAM-SHA-512");
        properties.put("sasl.jaas.config", environment.getProperty("sasl.jaas.config"));
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 9216000); // 9000KB
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");

        return new KafkaProducer<>(properties);
    }

    @Bean
    public ParallelStreamProcessor<String, Object> parallelStreamProcessor() {
        Consumer<String, Object> kafkaConsumer = getKafkaConsumer();
        Producer<String, Object> kafkaProducer = getKafkaProducer();
        var options = ParallelConsumerOptions.<String, Object>builder()
                                             .ordering(KEY)
                                             .maxConcurrency(CONSUMER_CONCURRENCY)
                                             .messageBufferSize(MESSAGE_BUFFER_SIZE)
                                             .consumer(kafkaConsumer)
                                             .producer(kafkaProducer)
                                             .meterRegistry(meterRegistry)
                                             .build();

        KafkaClientMetrics kafkaClientMetrics = new KafkaClientMetrics(kafkaConsumer);
        kafkaClientMetrics.bindTo(meterRegistry);
        ParallelStreamProcessor<String, Object> eosStreamProcessor =
                ParallelStreamProcessor.createEosStreamProcessor(options);

        eosStreamProcessor.subscribe(of(INPUT_TOPIC));

        return eosStreamProcessor;
    }

    @Bean
    public DefaultKafkaHeaderMapper kafkaHeaderMapper() {

        return new DefaultKafkaHeaderMapper() {
            @Override
            public void fromHeaders(MessageHeaders headers, Headers target) {
                super.fromHeaders(headers, target);
                target.remove("spring_json_header_types");
                target.remove("target-protocol");
                target.remove("contentType");
            }
        };
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }
}
