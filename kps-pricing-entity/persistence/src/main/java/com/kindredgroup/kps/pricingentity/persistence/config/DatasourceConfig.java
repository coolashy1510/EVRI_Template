package com.kindredgroup.kps.pricingentity.persistence.config;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

@Configuration
public class DatasourceConfig implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {

        Properties props = new Properties();
        props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        props.setProperty("dataSource.serverName", environment.getProperty("datasource.hostname", "localhost"));
        props.setProperty("dataSource.portNumber", environment.getProperty("datasource.port", "5432"));
        props.setProperty("dataSource.databaseName", environment.getProperty("datasource.db", "kps_pricing_entity"));
        props.setProperty("dataSource.user", environment.getProperty("datasource.username", "postgres"));
        props.setProperty("dataSource.password", environment.getProperty("datasource.password", "postgres"));
        props.put("dataSource.logWriter", new PrintWriter(System.out));

        HikariConfig config = new HikariConfig(props);
        if (environment.acceptsProfiles(Profiles.of("liquibase"))) {
            return new HikariDataSource(config);
        } else {
            return new LazyConnectionDataSourceProxy(new HikariDataSource(config));
        }

    }
}
