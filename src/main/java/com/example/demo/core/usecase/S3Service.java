package com.example.demo.core.usecase;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public S3Service(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket.upload}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    public String uploadFile(String directory, MultipartFile file) throws IOException {

        // Gera o caminho completo para o arquivo no diretório
        String fileName = directory + "/" + file.getOriginalFilename();

        // Cria um arquivo temporário no sistema de arquivos local
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile.toFile());

        // Envia o arquivo para o S3
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, tempFile.toFile()));

        // Apaga o arquivo temporário após o upload
        Files.delete(tempFile);

        return "File uploaded: " + fileName;
    }
}
