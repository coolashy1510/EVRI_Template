package com.kindredgroup.kps.pricingentity.persistence.config;

import javax.sql.DataSource;

import com.kindredgroup.kps.pricingentity.persistence.feeddomain.repository.ArchiveRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableAutoConfiguration
@Import(ArchiveRepository.class)
@EntityScan("")
public class TestDataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {

        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        DataSource dataSource = dataSourceBuilder.url(
                                                         "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;" +
                                                                 "NON_KEYWORDS=KEY,VALUE;" +
                                                                 "DATABASE_TO_LOWER=TRUE;DB_CLOSE_ON_EXIT=FALSE;" +
                                                                 "DEFAULT_NULL_ORDERING=HIGH")
                                                 .username("sa").password("").build();
        return dataSource;
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }


}
