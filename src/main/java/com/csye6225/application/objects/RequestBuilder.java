package com.csye6225.application.objects;

import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestBuilder {

    public static PublishRequest build(String topicArn, Message message) {
        Map<String, MessageAttributeValue> attributes = new HashMap<>();
        attributes.put("email", buildAttribute(message.getEmail(), "String"));
        attributes.put("token", buildAttribute(message.getToken(), "String"));
        attributes.put("messagetype", buildAttribute(message.getMessagetype(), "String"));

        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message("text message from ebby")
                .messageAttributes(attributes)
                .build();

        return request;
    }

    private static MessageAttributeValue buildAttribute(String value, String dataType) {
        return MessageAttributeValue.builder()
                .dataType(dataType)
                .stringValue(value)
                .build();
    }
}
