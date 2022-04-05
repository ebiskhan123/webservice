package com.csye6225.application;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricRegistry {

    private MeterRegistry meterRegistry;

    public MetricRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public MeterRegistry getInstance(){
        this.meterRegistry.config().commonTags("application","webservice");
        return this.meterRegistry;
    }

}