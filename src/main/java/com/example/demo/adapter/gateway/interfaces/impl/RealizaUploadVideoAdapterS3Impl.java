package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.RealizaUploadVideoAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class RealizaUploadVideoAdapterS3Impl implements RealizaUploadVideoAdapter {

    private final S3Client amazonS3;
    private final String bucketName;

    public RealizaUploadVideoAdapterS3Impl(S3Client amazonS3, @Value("${cloud.aws.s3.bucket.final}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    @Override
    public void execute(String directory, String filePath) throws IOException {

        final var putObject = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(directory + "/" + new File(filePath).getName())
                .contentType("application/zip")
                .build();

        amazonS3.putObject(putObject, RequestBody.fromFile(new File(filePath)));

        Files.delete(Path.of(filePath));
    }
}
