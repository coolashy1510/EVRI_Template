package com.kindredgroup.kps.pricingentity.config;

import com.kindredgroup.kps.metrics.util.MetricsHelper;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringConfig {
    @Bean
    public MetricsHelper metricsHelper(MeterRegistry meterRegistry) {
        return new MetricsHelper(meterRegistry);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry meterRegistry) {
        return new TimedAspect(meterRegistry);
    }

}
