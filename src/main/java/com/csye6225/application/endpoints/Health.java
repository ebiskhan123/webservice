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

    private DynamoDB dynamoDB;

    static String tableName = "emailTokenTbl";
    private AmazonDynamoDB client;
    @PostConstruct
    void init(){
        client = AmazonDynamoDBClientBuilder.standard().withCredentials(new InstanceProfileCredentialsProvider(false))
                .withRegion("us-east-1").build();
        dynamoDB = new DynamoDB(client);
    }

    @GetMapping()
    public ResponseEntity<?> getHealthz() {
        metricRegistry.getInstance().counter("Health get","csye6225","health endpoint").increment();
        LOGGER.info("Health endpoint called");
        return ResponseEntity.ok().body(null);
    }

    @GetMapping(value = "/health2")
    public ResponseEntity<?> getHealth(){

        Table table = dynamoDB.getTable(tableName);
        try {

            Item item = new Item().withString("email", "ebiskhan123")
                    .withLong("ttl",(System.currentTimeMillis() / 1000L)+ 60000)
                    .withString("emailid", "ebishkhan@gmail.com");
            table.putItem(item);

//            item = new Item().withPrimaryKey("Id", 121).withString("Title", "Book 121 Title")
//                    .withString("ISBN", "121-1111111111")
//                    .withStringSet("Authors", new HashSet<String>(Arrays.asList("Author21", "Author 22")))
//                    .withNumber("Price", 20).withString("Dimensions", "8.5x11.0x.75").withNumber("PageCount", 500)
//                    .withBoolean("InPublication", true).withString("ProductCategory", "Book");
//            table.putItem(item);

        }
        catch (Exception e) {
            System.err.println("Create items failed.");
            System.err.println(e.getMessage());

        }
        return ResponseEntity.ok().body(null);
    }

}
