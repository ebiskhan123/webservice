package com.csye6225.application.endpoints;

import com.csye6225.application.endpoints.Health;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class HealthTest {

    @Autowired
    Health health;

    @Test
    public void testInitioalization(){
        assertNotEquals(health,null);
    }

    @Test
    public void testHealthStatus(){
        assertEquals(health.getHealthz().getStatusCode(),200);
    }
}