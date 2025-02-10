package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.ListarVideosProcessadosAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListarVideosProcessadosAdapterImpl implements ListarVideosProcessadosAdapter {

    private final S3Client s3Client;
    private final String bucketName;

    public ListarVideosProcessadosAdapterImpl(S3Client s3Client, @Value("${cloud.aws.s3.bucket.final}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public List<String> listarArquivos() {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response result = s3Client.listObjectsV2(request);

        return result.contents().stream()
                .map(S3Object::key) // Pega apenas o nome dos arquivos
                .collect(Collectors.toList());
    }
}
