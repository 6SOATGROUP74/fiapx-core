package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.RealizaDownloadVideoAdapter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Component
public class RealizaDownloadVideoAdapterS3Impl implements RealizaDownloadVideoAdapter {

    private final S3Client s3Client;

    public RealizaDownloadVideoAdapterS3Impl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @SneakyThrows
    @Override
    public File execute(String bucket, String key) {
        System.out.printf("Baixando arquivo do S3: %s / %s", bucket, key);
        File tempFile = File.createTempFile("s3file", ".tmp");

        try (OutputStream os = new FileOutputStream(tempFile)) {
            s3Client.getObject(builder -> builder.bucket(bucket).key(key)).transferTo(os);
        }

        return tempFile;
    }
}
