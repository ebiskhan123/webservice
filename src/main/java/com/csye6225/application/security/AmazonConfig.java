package com.csye6225.application.security;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {
    @Bean
    public AmazonS3 s3() {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new InstanceProfileCredentialsProvider(false))
                .withRegion("us-east-1")
                .build();

        return s3;
//                AmazonS3ClientBuilder
//                .standard()
//                .withRegion("us-east-1")
//                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
//                .build();

    }
}
