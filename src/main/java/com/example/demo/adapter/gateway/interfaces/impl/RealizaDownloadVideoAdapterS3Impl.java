package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.RealizaDownloadVideoAdapter;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;

@Component
public class RealizaDownloadVideoAdapterS3Impl implements RealizaDownloadVideoAdapter {

    private static final Logger logger = LogManager.getLogger(RealizaDownloadVideoAdapterS3Impl.class);

    private final S3Client s3Client;

    public RealizaDownloadVideoAdapterS3Impl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @SneakyThrows
    @Override
    public synchronized File execute(String bucket, String key) {
        logger.info("m=execute, status=init, msg=Baixando vídeo do bucket S3={}, nomeDoArquivo={}", bucket, key);

        File tempFile = Files.createTempFile("$", "$" + key.replace("/", "_")).toFile();

        try (OutputStream os = new FileOutputStream(tempFile)) {
            s3Client.getObject(builder -> builder.bucket(bucket).key(key)).transferTo(os);
        }

        logger.info("m=execute, status=success, msg=Arquivo baixado do bucket S3 com sucesso!");
        return tempFile;
    }
}
