package com.example.demo.adapter.gateway.interfaces.impl;

import com.example.demo.adapter.gateway.interfaces.ConverteVideoFrameAdapter;
import com.example.demo.adapter.gateway.interfaces.ConverteVideoZipAdapter;
import com.example.demo.adapter.gateway.interfaces.GerenciaStatusVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.ProcessaVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.RealizaDownloadVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.RealizaUploadVideoAdapter;
import com.example.demo.adapter.presenter.S3Message;
import com.example.demo.config.Commons;
import com.example.demo.core.domain.StatusProcessamento;
import com.example.demo.core.domain.Video;
import com.example.demo.core.usecase.ConverteFileEmMultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

import static io.awspring.cloud.sqs.annotation.SqsListenerAcknowledgementMode.ON_SUCCESS;

@Component
public class ProcessaVideoAdapterSqsImpl implements ProcessaVideoAdapter {

    private static final Logger logger = LogManager.getLogger(ProcessaVideoAdapterSqsImpl.class);

    @Value("${diretorio.saida.zip}")
    String outputDirPath;

    @Value("${cloud.aws.s3.bucket.upload}")
    String bucketDeDownload;

    ConverteVideoZipAdapter converteVideoZipAdapter;
    ConverteVideoFrameAdapter converteVideoFrameAdapter;
    RealizaUploadVideoAdapter realizaUploadVideoAdapter;
    RealizaDownloadVideoAdapter realizaDownloadVideoAdapter;
    GerenciaStatusVideoAdapter gerenciaStatusVideoAdapter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProcessaVideoAdapterSqsImpl(ConverteVideoZipAdapter converteVideoZipAdapter, ConverteVideoFrameAdapter converteVideoFrameAdapter, RealizaUploadVideoAdapter realizaUploadVideoAdapter, RealizaDownloadVideoAdapter realizaDownloadVideoAdapter, GerenciaStatusVideoAdapter gerenciaStatusVideoAdapter) {
        this.converteVideoZipAdapter = converteVideoZipAdapter;
        this.converteVideoFrameAdapter = converteVideoFrameAdapter;
        this.realizaUploadVideoAdapter = realizaUploadVideoAdapter;
        this.realizaDownloadVideoAdapter = realizaDownloadVideoAdapter;
        this.gerenciaStatusVideoAdapter = gerenciaStatusVideoAdapter;
    }

    @SneakyThrows
    @Override
    @SqsListener(value = "upload-file-fiapx.fifo", acknowledgementMode = ON_SUCCESS, messageVisibilitySeconds = "300")
    public void execute(String mensagem) {

        logger.info("m=execute, status=init, msg=Mensagem de processamento de vídeo recebida={}", mensagem);

        try {
            JsonNode jsonNode = objectMapper.readTree(mensagem);

            String chaveArquivo = URLDecoder.decode(jsonNode.get("key").asText(), StandardCharsets.UTF_8);


            String[] parts = chaveArquivo.split("/", 2);
            String email = parts[0];
            String nomeArquivo = parts[1];

            S3Message s3Message = new S3Message();
            s3Message.setKey(nomeArquivo);
            s3Message.setEmail(email);

            logger.info("m=execute, status=process, msg=Download arquivo bucket ={}", nomeArquivo);

            //baixa arquivo
            File arquivoBaixado = realizaDownloadVideoAdapter.execute(bucketDeDownload, chaveArquivo);

            logger.info("m=execute, status=process, msg=Download concluido com sucesso ={}", nomeArquivo);

            //Status atual do video
            final var videoDomain = VideoDomainFactory.criarVideo(arquivoBaixado, email);

            gerenciaStatusVideoAdapter.salvaVideo(videoDomain);

            logger.info("m=execute, status=process, msg=Video recebido com sucesso !={}", nomeArquivo);

            MultipartFile arquivoEmMultipartFile = new ConverteFileEmMultipartFile(arquivoBaixado);

            gerenciaStatusVideoAdapter.alteraStatus(videoDomain.getId(), StatusProcessamento.EM_PROCESSO);

            converteVideoFrameAdapter.execute(arquivoEmMultipartFile);

            logger.info("m=execute, status=process, msg=Video convertido em frames !={}", nomeArquivo);

            String nomeArquivoZipado = converteVideoZipAdapter.execute(nomeArquivo);

            logger.info("m=execute, status=process, msg=Arquivo zipado !={}", nomeArquivo);

            realizaUploadVideoAdapter.execute(email, nomeArquivoZipado);

            gerenciaStatusVideoAdapter.alteraStatus(videoDomain.getId(), StatusProcessamento.CONCLUIDO);

            logger.info("m=execute, status=success, msg=Video processado com sucesso={}", mensagem);
        } catch (Exception e) {
            logger.error("m=execute, status=error, msg=Mensagem de processamento de vídeo falhou mensagem={} exception={}", mensagem, e.getMessage());
            throw e;
        }finally {
            Path pathZipsOut = Path.of("./zips_output");
            Path pathFramesOut = Path.of("./frames_output");
            Commons.limpaDiretorio(pathZipsOut);
            Commons.limpaDiretorio(pathFramesOut);
        }
    }
}