package com.example.demo.adapter.gateway.interfaces.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.demo.adapter.gateway.interfaces.RealizaUploadVideoAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class RealizaUploadVideoAdapterS3Impl implements RealizaUploadVideoAdapter {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public RealizaUploadVideoAdapterS3Impl(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket.final}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    @Override
    public void execute(String directory, MultipartFile file) throws IOException {
        // Gera o caminho completo para o arquivo no diretório
        String fileName = directory + "/" + file.getOriginalFilename();

        // Cria um arquivo temporário no sistema de arquivos local
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());

        // Envia o arquivo para o S3
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, tempFile.toFile()));

        // Apaga o arquivo temporário após o upload
        Files.delete(tempFile);
    }
}
