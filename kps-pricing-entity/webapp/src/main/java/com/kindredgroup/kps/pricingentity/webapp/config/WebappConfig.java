package com.kindredgroup.kps.pricingentity.webapp.config;

import com.kindredgroup.kps.pricingentity.webapp.messaging.consumer.MessageRoutingProperties;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(MessageRoutingProperties.class)
public class WebappConfig {

    @Bean
    Tracer tracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("com.kindredgroup.kps.pricingentity");
    }
}
