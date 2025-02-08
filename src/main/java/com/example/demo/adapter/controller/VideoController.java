package com.example.demo.adapter.controller;

import com.example.demo.core.domain.Pessoa;
import com.example.demo.infrastructure.repository.dynamo.PessoaRepository;
import com.example.demo.core.usecase.SqsService;
import com.example.demo.core.usecase.ZipUtilsService;
import com.example.demo.core.usecase.VideoFrameExtractorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/video")
public class VideoController {


    private final VideoFrameExtractorService frameExtractorService;
    private final ZipUtilsService zipUtilsService;
    private final SqsService sqsService;
    private final PessoaRepository pessoaRepository;


    public VideoController(VideoFrameExtractorService frameExtractorService, ZipUtilsService zipUtilsService, SqsService sqsService, PessoaRepository pessoaRepository) {
        this.frameExtractorService = frameExtractorService;
        this.zipUtilsService = zipUtilsService;
        this.sqsService = sqsService;
        this.pessoaRepository = pessoaRepository;
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




    @PostMapping(value = "/pessoa/salvar")
    public String salvarPessoa(@RequestBody Pessoa pessoa) {
        pessoa.setId(UUID.randomUUID().toString());
        pessoaRepository.save(pessoa);
        return "Pessoa salva!";
    }

    @GetMapping("pessoa/{id}")
    public Optional<Pessoa> buscarPessoa(@PathVariable String id) {
        return pessoaRepository.findById(id);
    }

//    @PostMapping(value = "/range", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> realizaUploadDeVideo(@RequestPart("file") MultipartFile file,
//                                                  @RequestParam("startTime") double startTime,
//                                                  @RequestParam("endTime") double endTime) {
//        try {
//            // Salva o vídeo temporariamente
//            File tempFile = File.createTempFile("uploaded_", ".mp4");
//            file.transferTo(tempFile);
//
//            // Define o diretório de saída para os frames
//            String outputDir = "frames_output";
//            new File(outputDir).mkdirs();
//            // Extrai os frames dentro do intervalo solicitado
//            frameExtractorService.extractFramesInRange(tempFile.getAbsolutePath(), outputDir, startTime, endTime);
//
//        } catch (IOException e) {
//            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar o vídeo: " + e.getMessage());
//        }
//        return ResponseEntity.status(HttpStatus.CREATED).body("Evento recebido: " );
//    }
}
