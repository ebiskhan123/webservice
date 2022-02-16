package com.csye6225.application;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan(basePackages = { "com.csye6225.*"})
@EnableJpaRepositories
public class Driver {
    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);
    public static void main(String[] args) {
        SpringApplication.run(Driver.class, args);
        LOGGER.info("Application started");
    }
}





