package com.example.demo.adapter.controller;

import com.example.demo.adapter.gateway.interfaces.GerenciaStatusVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.RealizaDownloadVideoAdapter;
import com.example.demo.adapter.gateway.interfaces.impl.ListarVideosProcessadosAdapterImpl;
import com.example.demo.adapter.presenter.S3Message;
import com.example.demo.core.domain.Video;
import com.example.demo.core.domain.VideoDto;
import com.example.demo.infrastructure.repository.dynamo.VideoRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

@RestController
@RequestMapping("/v1/video")
public class VideoController {

    private final RealizaDownloadVideoAdapter realizaDownloadVideoAdapter;
    private final ListarVideosProcessadosAdapterImpl listarVideosProcessadosAdapter;
    private final GerenciaStatusVideoAdapter gerenciaStatusVideoAdapter;

    public VideoController(RealizaDownloadVideoAdapter realizaDownloadVideoAdapter, ListarVideosProcessadosAdapterImpl listarVideosProcessadosAdapter, GerenciaStatusVideoAdapter gerenciaStatusVideoAdapter) {
        this.realizaDownloadVideoAdapter = realizaDownloadVideoAdapter;
        this.listarVideosProcessadosAdapter = listarVideosProcessadosAdapter;
        this.gerenciaStatusVideoAdapter = gerenciaStatusVideoAdapter;
    }

    @SneakyThrows
    @PostMapping(value = "/download-de-arquivo", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadDeArquivo(@RequestBody S3Message s3Message) {
        File file = realizaDownloadVideoAdapter.execute("fiapx-bucket-upload-final", s3Message.getKey());

        String novoNomeArquivo = file.getName().replace(".mp4", ".zip").replaceAll("^\\$[^\\$]*\\$", "");

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));



        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + novoNomeArquivo + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }

    @GetMapping("/{email}")
    @Operation(summary = "Listar arquivos no bucket", description = "Retorna uma lista com os nomes dos arquivos disponíveis no S3")
    public ResponseEntity<List<VideoDto>> listarArquivos(@PathVariable final String email) {
        List<VideoDto> arquivos = gerenciaStatusVideoAdapter.listarVideo(email);
        return ResponseEntity.ok(arquivos);
    }
}
