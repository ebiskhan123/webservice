package com.csye6225.application.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Component
public class BucketCreated {

    @Value("${cloud.aws.bucketname}")
    private String bucketName;
}
