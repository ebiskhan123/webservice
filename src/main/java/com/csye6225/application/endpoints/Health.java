package com.csye6225.application.endpoints;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping("/api/healthz")
public class Health {

    @GetMapping()
    public String getHealthz() {
        return "äll good";
    }

}
