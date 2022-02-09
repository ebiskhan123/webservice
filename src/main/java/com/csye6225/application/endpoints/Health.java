package com.csye6225.application.endpoints;

import com.csye6225.application.objects.HealthStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping("/api/healthz")
public class Health {

    // assignment 1 api
    @GetMapping()
    public HealthStatus getHealthz() {
        return new HealthStatus("äll good");
    }

}
