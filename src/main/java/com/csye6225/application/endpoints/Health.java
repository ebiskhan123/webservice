package com.csye6225.application.endpoints;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.csye6225.application.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;

@Service
@RestController
@RequestMapping("/healthz")
public class Health {
    private static final Logger LOGGER = LoggerFactory.getLogger(Health.class);
    // assignment 1 api

    @Autowired
    MetricRegistry metricRegistry;

//    @Autowired
//    AmazonDynamoDB client;


    @GetMapping()
    public ResponseEntity<?> getHealthz() {
        metricRegistry.getInstance().counter("Health get","csye6225","health endpoint").increment();
        LOGGER.info("Health endpoint called");
        return ResponseEntity.ok().body(null);
    }

    @GetMapping(value = "/health2")
    public ResponseEntity<?> getHealth(){

        return ResponseEntity.ok().body(null);
    }

}
