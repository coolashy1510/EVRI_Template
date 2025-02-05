package com.kindredgroup.kps.pricingentity.persistence.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = {"com.kindredgroup.kps.pricingentity.persistence.coreentity.model",
        "com.kindredgroup.kps.pricingentity.persistence.feeddomain.model"})
@EnableJpaRepositories(basePackages = {"com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository",
        "com.kindredgroup.kps.pricingentity.persistence.coreentity.repository",
        "com.kindredgroup.kps.pricingentity.persistence.pricingdomain.repository"})
@EnableJpaAuditing
@EnableTransactionManagement // JDK dynamic proxies
public class RepositoryConfig implements EnvironmentAware {
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
