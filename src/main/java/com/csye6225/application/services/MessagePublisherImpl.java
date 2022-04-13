package com.csye6225.application.services;

import com.csye6225.application.objects.Message;
import com.csye6225.application.objects.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SnsResponse;

import javax.annotation.PostConstruct;

@Service
public class MessagePublisherImpl implements MessagePublisher {
    private final static Logger LOG = LoggerFactory.getLogger(MessagePublisherImpl.class);

    private SnsClient snsClient;
    private String topicArn ="arn:aws:sns:us-east-1:556795868226:UserVerificationTopic";
    @PostConstruct
    public void init() {
        this.snsClient = SnsClient.builder().region(Region.US_EAST_1).credentialsProvider(software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider.builder().build()).build();
    }

    @Override
    public void publish(Message message) {

        try {
            PublishRequest request = RequestBuilder.build(topicArn, message);
            LOG.info("Request: {}", request);

            PublishResponse publishResponse = snsClient.publish(request);
            LOG.info("Publish response: {}", publishResponse);

//            SdkHttpResponse httpResponse = publishResponse.sdkHttpResponse();
//            response = new SnsResponse(
//                    httpResponse.statusCode(),
//                    httpResponse.statusText().orElse(null),
//                    publishResponse.messageId());
//            LOG.info("Response details: {}", response);
        } catch (Exception e) {
            LOG.error(e.toString());
        }
    }
}