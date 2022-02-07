package com.csye6225.application;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.csye6225.*"})
public class Driver {
    public static void main(String[] args) {
        SpringApplication.run(Driver.class, args);
    }
}

