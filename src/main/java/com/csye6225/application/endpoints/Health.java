package com.csye6225.application.endpoints;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping("/healthz")
public class Health {

    // assignment 1 api
    @GetMapping()
    public ResponseEntity<?> getHealthz() {
        return ResponseEntity.ok().body(null);
    }

}
