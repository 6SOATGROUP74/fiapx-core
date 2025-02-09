package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.ConverteVideoZipAdapter;
import com.example.demo.adapter.gateway.interfaces.ConverteVideoFrameAdapter;
import com.example.demo.adapter.gateway.interfaces.ProcessaVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.RealizaDownloadVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.RealizaUploadVideoAdapter;
import com.example.demo.adapter.presenter.S3Message;
import com.example.demo.core.usecase.ConverteFileEmMultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
public class ProcessaVideoAdapterSqsImpl implements ProcessaVideoAdapter {

    ConverteVideoZipAdapter converteVideoZipAdapter;
    ConverteVideoFrameAdapter converteVideoFrameAdapter;
    RealizaUploadVideoAdapter realizaUploadVideoAdapter;
    RealizaDownloadVideoAdapter realizaDownloadVideoAdapter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public void execute(String mensagem) {

        try {
            // Converter JSON para objeto
            JsonNode jsonNode = objectMapper.readTree(mensagem);
            //bucketOrigem
            String bucketOrigem = jsonNode.get("bucket").asText();

            //chaveArquivo
            String chaveArquivo = jsonNode.get("key").asText();

            S3Message s3Message = new S3Message();
            s3Message.setBucket(bucketOrigem);
            s3Message.setKey(chaveArquivo);

            //bucketDestino
            String bucketDestino = jsonNode.get("bucketDestino").asText();

            //baixa arquivo
            File arquivoBaixado = realizaDownloadVideoAdapter.execute("meu-bucket", "zips/arquivos.zip");

            MultipartFile arquivoEmMultipartFile = new ConverteFileEmMultipartFile(arquivoBaixado);

            converteVideoFrameAdapter.execute(arquivoEmMultipartFile);

            //Converter
            converteVideoZipAdapter.execute()

            //realizaUploadVideoAdapter.execute("ss", "");

            String nomeDoArquivoZip = "nomeDoArquivoZip";

            //MultipartFile arquivoZipado = converteVideoAdapter.execute(nomeDoArquivoZip);
            //realizaUploadVideoAdapter.execute("diretorio_concluído_do_bucket", arquivoZipado);


        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem do SQS: " + e.getMessage());
        }
    }
}