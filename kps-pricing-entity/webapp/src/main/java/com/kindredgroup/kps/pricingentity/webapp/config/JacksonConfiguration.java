package com.kindredgroup.kps.pricingentity.webapp.config;

import com.kindredgroup.kps.internal.jackson.KpsObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public KpsObjectMapper objectMapper() {
        return new KpsObjectMapper();
    }
}
