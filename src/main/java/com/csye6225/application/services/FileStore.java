package com.csye6225.application.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.csye6225.application.security.BucketCreated;
import com.csye6225.application.security.BucketName;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FileStore {

//    @Value("cloud.aws.bucketname")
//    private String bucketName;

    @Autowired
    BucketCreated bucketCreated;

    private final AmazonS3 amazonS3;

    public String upload(String path,
                         String fileName,
                         Optional<Map<String, String>> optionalMetaData,
                         InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        try {
            amazonS3.putObject(path, fileName, inputStream, objectMetadata);
            return ((AmazonS3Client) amazonS3).getResourceUrl( bucketCreated.getBucketName(), fileName);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }
    public void delete(String path,
                       String fileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        try {
            amazonS3.deleteObject(path,fileName);

        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to delete the file", e);
        }
    }
}
