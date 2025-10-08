package com.example.internshipproject.InventoryManagementV2.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.example.internshipproject.InventoryManagementV2"})
@RequiredArgsConstructor
public class DataSourceConfig {
    private final Environment props;

    @Bean(name = "masterJdbcTemplate")
    public JdbcTemplate masterJdbcTemplate(@Qualifier("master") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "slaveJdbcTemplate")
    public JdbcTemplate slaveJdbcTemplate(@Qualifier("slave") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    private HikariDataSource abstractDataSource() {
        var abstractDataSource = new HikariDataSource();
        abstractDataSource.setDriverClassName(props.getProperty("datasource.driver-class-name"));
        return abstractDataSource;
    }

    /**
     * master setting
     */
    @Bean(destroyMethod = "close", name="master")
    @Primary
    public HikariDataSource masterDataSource() {
        var masterDataSource = abstractDataSource();
        String url = props.getProperty("datasource.master.url");
        String username = props.getProperty("datasource.master.username");
        String password = props.getProperty("datasource.master.password");

        log.info("Master DB URL: " + url);  // Add logging
        log.info("Master DB Username: " + username);  // Add logging

        if (url == null || username == null || password == null) {
            throw new IllegalArgumentException("One or more required properties for master DB are null");
        }

        masterDataSource.setJdbcUrl(url);
        masterDataSource.setUsername(username);
        masterDataSource.setPassword(password);
        return masterDataSource;
    }

    /**
     * slave setting
     */
    @Bean(destroyMethod = "close", name="slave")
    public HikariDataSource slaveDataSource() {
        var slaveDataSource = abstractDataSource();

        String url = props.getProperty("datasource.slave.url");
        String username = props.getProperty("datasource.slave.username");
        String password = props.getProperty("datasource.slave.password");

        log.info("Slave DB URL: " + url);  // Add logging
        log.info("Slave DB Username: " + username);  // Add logging
        if (url == null || username == null || password == null) {
            throw new IllegalArgumentException("One or more required properties for slave DB are null");
        }

        slaveDataSource.setJdbcUrl(url);
        slaveDataSource.setUsername(username);
        slaveDataSource.setPassword(password);
        return slaveDataSource;
    }

    @Bean(name="dynamicDataSource")
    public DataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource());
        targetDataSources.put("slave", slaveDataSource());

        AbstractRoutingDataSource dynamicDataSource = new AbstractRoutingDataSource() {
            @Override
            protected Object determineCurrentLookupKey() {
////                String lookupKey = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "slave" : "master";
////                log.info("connected DataSource :" + lookupKey);
////                return lookupKey;
//                String manualKey = DataSourceContextHolder.getDataSourceKey();
//                if (manualKey != null) {
//                    log.info("Manual routing to: " + manualKey);
//                    return manualKey;
//                }

                String lookupKey = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "slave" : "master";
                log.info("Auto-routing to: " + lookupKey);
                return lookupKey;
            }
        };

        dynamicDataSource.setDefaultTargetDataSource(targetDataSources.get("master"));
        dynamicDataSource.setTargetDataSources(targetDataSources);
        return dynamicDataSource;
    }

    @Bean
    public DataSource dataSource() {
        return new LazyConnectionDataSourceProxy(dynamicDataSource());
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource())
                .packages("com.example.internshipproject.InventoryManagementV2.entities")
                .build();
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory(builder).getObject()));
    }
}