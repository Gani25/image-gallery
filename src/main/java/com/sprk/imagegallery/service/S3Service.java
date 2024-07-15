package com.sprk.imagegallery.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

        private S3Client s3Client;

        private String bucketName;
        private String region;

        public S3Service(@Value("${aws.accessKeyId}") String accessKey,
                        @Value("${aws.secretAccessKey}") String secretKey,
                        @Value("${aws.region}") String region,
                        @Value("${aws.s3.bucketName}") String bucketName) {

                this.bucketName = bucketName;
                this.region = region;

                AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey,
                                secretKey);
                this.s3Client = S3Client.builder()
                                .region(Region.of(region))
                                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                                .build();
        }

        public String uploadFile(MultipartFile file) throws IOException {
                String key = "images/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
                s3Client.putObject(
                                PutObjectRequest
                                                .builder()
                                                .bucket(bucketName)
                                                .key(key)
                                                .acl(ObjectCannedACL.PUBLIC_READ)
                                                .build(),
                                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
                // RequestBody.fromBytes(file.getBytes())

                );
                // Set ACL to public-read
                // s3Client.putObjectAcl(
                // PutObjectAclRequest.builder()
                // .bucket(bucketName)
                // .key(key)
                // .acl("public-read")
                // .build());

                String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);

                return url;
        }

        public void deleteFile(String imageUrl) {

                String key = imageUrl.substring(imageUrl.lastIndexOf("/images") + 1);

                s3Client.deleteObject(DeleteObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build());

        }
}
