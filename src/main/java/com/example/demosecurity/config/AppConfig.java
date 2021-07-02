package com.example.demosecurity.config;


import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com")
@PropertySource("classpath:persistence-mysql.properties")
public class AppConfig {

    @Autowired
    private Environment env;

    @Bean
    public DataSource securityDataSource() {
        ComboPooledDataSource comboPooledDataSource =
                new ComboPooledDataSource();
        try {
            comboPooledDataSource.setDriverClass(env.getProperty("jdbc.driver"));
            comboPooledDataSource.setJdbcUrl(env.getProperty("jdbc.url"));
            comboPooledDataSource.setUser(env.getProperty("jdbc.user"));
            comboPooledDataSource.setPassword(env.getProperty("jdbc.password"));
            comboPooledDataSource.setInitialPoolSize(getProperty("connection.pool.initialPoolSize"));
            comboPooledDataSource.setMinPoolSize(getProperty("connection.pool.minPoolSize"));
            comboPooledDataSource.setMaxPoolSize(getProperty("connection.pool.maxPoolSize"));
            comboPooledDataSource.setMaxIdleTime(getProperty("connection.pool.maxIdleTime"));

        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        return comboPooledDataSource;
    }

    private int getProperty(String propName) {
        String propVal = env.getProperty(propName);
        int intPropVal = Integer.parseInt(propVal);
        return intPropVal;
    }
}
