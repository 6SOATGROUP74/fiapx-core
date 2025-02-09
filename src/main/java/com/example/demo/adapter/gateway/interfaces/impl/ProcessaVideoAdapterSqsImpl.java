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
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;

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
    @SqsListener("upload-file-fiapx")
    public void execute(String mensagem) {

        logger.info("m=execute, status=init, msg=Mensagem de processamento de vídeo recebida={}", mensagem);

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

            //baixa arquivo
            File arquivoBaixado = realizaDownloadVideoAdapter.execute(bucketDeDownload, chaveArquivo);

            //Status atual do video
            Video videoRecebido = new Video();
            videoRecebido.setId(UUID.randomUUID().toString());
            videoRecebido.setNome(arquivoBaixado.getName());
            videoRecebido.setStatus(StatusProcessamento.PENDENTE.toString());
            videoRecebido.setDataCriacao(Instant.now().toString());
            videoRecebido.setDataAtualizacao(Instant.now().toString());

            gerenciaStatusVideoAdapter.salvaVideo(videoRecebido);

            MultipartFile arquivoEmMultipartFile = new ConverteFileEmMultipartFile(arquivoBaixado);

            converteVideoFrameAdapter.execute(arquivoEmMultipartFile);

            //Converter em Zip
            MultipartFile arquivoZipado = converteVideoZipAdapter.execute(arquivoEmMultipartFile);

            //Realiza Upload no S3
            realizaUploadVideoAdapter.execute(outputDirPath, arquivoZipado);

            //Finaliza processamento e altera para Status concluído
            gerenciaStatusVideoAdapter.alteraStatus(videoRecebido.getId(), StatusProcessamento.CONCLUIDO);

            //Limpa repositórios após manipulação de arquivos
            Path pathZipsOut = Path.of("./zips_output");
            Path framesOut = Path.of("./frames_output");
            Commons.limpaDiretorio(pathZipsOut);
            Commons.limpaDiretorio(framesOut);
            logger.info("m=execute, status=success, msg=Video processado com sucesso={}", mensagem);
        } catch (Exception e) {
            logger.error("m=execute, status=error, msg=Mensagem de processamento de vídeo falhou mensagem={} exception={}", mensagem, e.getMessage());;
        }
    }
}