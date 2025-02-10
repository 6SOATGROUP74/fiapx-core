package com.example.demo.adapter.controller;

import com.example.demo.adapter.gateway.interfaces.RealizaDownloadVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.impl.ListarVideosProcessadosAdapterImpl;
import com.example.demo.adapter.presenter.S3Message;
import com.example.demo.core.usecase.SqsServiceDefinitivo;
import com.example.demo.core.usecase.SqsService;
import com.example.demo.infrastructure.repository.dynamo.VideoRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/v1/video")
public class VideoController {

    private final SqsService sqsService;
    private final VideoRepository videoRepository;
    private final SqsServiceDefinitivo sqsServiceDefinitivo;
    private final RealizaDownloadVideoAdapter realizaDownloadVideoAdapter;
    private final ListarVideosProcessadosAdapterImpl listarVideosProcessadosAdapter;

    public VideoController(SqsService sqsService, VideoRepository videoRepository, SqsServiceDefinitivo sqsServiceDefinitivo, RealizaDownloadVideoAdapter realizaDownloadVideoAdapter, ListarVideosProcessadosAdapterImpl listarVideosProcessadosAdapter) {
        this.sqsService = sqsService;
        this.videoRepository = videoRepository;
        this.sqsServiceDefinitivo = sqsServiceDefinitivo;
        this.realizaDownloadVideoAdapter = realizaDownloadVideoAdapter;
        this.listarVideosProcessadosAdapter = listarVideosProcessadosAdapter;
    }

    @SneakyThrows
    @PostMapping(value = "/download-de-arquivo", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> downloadDeArquivo(@RequestBody S3Message s3Message) {
        File file = realizaDownloadVideoAdapter.execute(s3Message.getBucket(), s3Message.getKey());

        String novoNomeArquivo = file.getName().replace(".mp4", ".zip").replaceAll("^\\$[^\\$]*\\$", "");

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + novoNomeArquivo + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }

    @GetMapping("/listar-videos-processados")
    @Operation(summary = "Listar arquivos no bucket", description = "Retorna uma lista com os nomes dos arquivos disponíveis no S3")
    public ResponseEntity<List<String>> listarArquivos() {
        List<String> arquivos = listarVideosProcessadosAdapter.listarArquivos();
        return ResponseEntity.ok(arquivos);
    }
}
