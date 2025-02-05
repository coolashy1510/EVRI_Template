package com.kindredgroup.kps.pricingentity.webapp.rest.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class EntityManagerClientConfig {

    @Value("${http.entity-manager.url}")
    private String entityManagerUrl;

    @Bean
    public WebClient entityManagerWebClient() {
        return WebClient.builder()
                .baseUrl(entityManagerUrl)
                .build();
    }
}
