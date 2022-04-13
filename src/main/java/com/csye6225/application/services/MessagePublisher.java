package com.csye6225.application.services;

import com.csye6225.application.objects.Message;
import software.amazon.awssdk.services.sns.model.SnsResponse;

public interface MessagePublisher {
    void publish(Message message);
}
