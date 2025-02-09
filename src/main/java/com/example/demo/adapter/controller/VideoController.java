package com.example.demo.adapter.controller;

import com.example.demo.adapter.gateway.interfaces.RealizaDownloadVideoAdapter;
import com.example.demo.adapter.presenter.S3Message;
import com.example.demo.core.usecase.SqsServiceDefinitivo;
import com.example.demo.infrastructure.repository.entity.VideoEntity;
import com.example.demo.core.usecase.SqsService;
import com.example.demo.core.usecase.ZipUtilsService;
import com.example.demo.core.usecase.VideoFrameExtractorService;
import com.example.demo.infrastructure.repository.dynamo.VideoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/v1/video")
public class VideoController {


    private final VideoFrameExtractorService frameExtractorService;
    private final ZipUtilsService zipUtilsService;
    private final SqsService sqsService;
    private final VideoRepository videoRepository;
    private final SqsServiceDefinitivo sqsServiceDefinitivo;
    private final RealizaDownloadVideoAdapter realizaDownloadVideoAdapter;


    public VideoController(VideoFrameExtractorService frameExtractorService, ZipUtilsService zipUtilsService, SqsService sqsService, VideoRepository videoRepository, SqsServiceDefinitivo sqsServiceDefinitivo, RealizaDownloadVideoAdapter realizaDownloadVideoAdapter) {
        this.frameExtractorService = frameExtractorService;
        this.zipUtilsService = zipUtilsService;
        this.sqsService = sqsService;
        this.videoRepository = videoRepository;
        this.sqsServiceDefinitivo = sqsServiceDefinitivo;
        this.realizaDownloadVideoAdapter = realizaDownloadVideoAdapter;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> realizaUploadDeVideo(@RequestPart("file") MultipartFile file) {

        try {
            // Salva o vídeo temporariamente
            File tempFile = File.createTempFile("uploaded_", ".mp4");
            file.transferTo(tempFile);

            // Define o diretório de saída para os frames
            String outputDir = "frames_output";
            new File(outputDir).mkdirs();

            //Define onde será extraído o zip
            String outputDirZip = "zips"; // Pasta onde o ZIP será salvo
            String zipFileName = "frames_output.zip"; // Nome do ZIP

            // Extrai os frames
            frameExtractorService.extractFrames(tempFile.getAbsolutePath(), outputDir, outputDirZip, zipFileName);

            System.out.println("Frames extraídos com sucesso! Verifique o diretório: " + outputDir);
        } catch (IOException e) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar o vídeo: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Evento recebido: " );
    }

    @PostMapping(value = "/download-de-arquivo", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> downloadDeArquivo(@RequestBody S3Message s3Message) {
        File file = realizaDownloadVideoAdapter.execute(s3Message.getBucket(), s3Message.getKey());

        return ResponseEntity.ok().body(file);
    }


    @PostMapping(value = "/sqs-teste")
    public ResponseEntity<?> enviaMensagem(@RequestBody String mensagem) {

        sqsService.enviaMensagem(mensagem);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/sqs-teste-envia-arquivo")
    public ResponseEntity<?> enviaMensagem(String bucketName, String fileName, String bucketDestino) throws IOException {

        sqsService.enviarMensagemParaFila(bucketName, fileName, bucketDestino);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/sqs-teste-envia-arquivo-fluxo-real")
    public ResponseEntity<?> enviaMensagem(@RequestBody S3Message s3Message){

        sqsServiceDefinitivo.enviarMensagemParaFila(s3Message.getBucket(), s3Message.getKey(), "teste-de-envio");

        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/testa-implementacao-fluxo")
    public ResponseEntity<?> enviaMensagemParaFluxoImplementado(@RequestBody S3Message s3Message){

        sqsServiceDefinitivo.enviarMensagemParaFilaFluxoPronto(s3Message.getBucket(), s3Message.getKey(), "teste-de-envio");

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/video/salvar")
    public String salvarPessoa(@RequestBody VideoEntity video) {
        video.setId(UUID.randomUUID().toString());
        video.setStatus("PENDENTE");
        video.setDataCriacao(Instant.now().toString());
        video.setDataAtualizacao(Instant.now().toString());
        videoRepository.save(video);
        return "Video salvo!";
    }
}
