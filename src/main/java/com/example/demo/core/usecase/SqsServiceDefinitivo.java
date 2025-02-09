package com.example.demo.core.usecase;

import com.example.demo.adapter.presenter.S3Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class SqsServiceDefinitivo {

    private static final String SQS_URL = "http://localhost:4566/000000000000/minha-fila";
    private static final Logger logger = LoggerFactory.getLogger(SqsServiceDefinitivo.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SqsTemplate sqsTemplate;

    private final S3Client s3Client;

    public SqsServiceDefinitivo(S3Client s3Client) {
        this.s3Client = s3Client;
    }


    public void enviaMensagem(String mensagem) {

        sqsTemplate.send(to -> to.queue("filaTeste").payload(mensagem));
    }

    public void enviarMensagemParaFila(String bucketName, String fileName, String bucketDestino) {
        // URL do arquivo no S3
        //String s3Url = String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
        //String s3Url = String.format("http://localhost:4566/%s/%s", bucketName, fileName);
        String s3Url = String.format("{\"bucket\": \"%s\", \"key\": \"%s\", \"bucketDestino\": \"%s\"}",
                bucketName, fileName, bucketDestino);

        // Enviar a URL para a fila do SQS
        sqsTemplate.send("fila-real-de-teste", s3Url);
    }

//    @SqsListener("filaTeste")
//    public void consomeEvento(String mensagem) {
//        logger.info("Mensagem recebida - {}", mensagem);
//    }

    @SqsListener("fila-real-de-teste")
    public void processarMensagem(String mensagem) {
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
            File arquivoBaixado = downloadFileFromS3("meu-bucket", "zips/arquivos.zip");

            // Copiar o arquivo de um bucket para outro
            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketOrigem)
                    .sourceKey(chaveArquivo)
                    .destinationBucket(bucketDestino)
                    .destinationKey(chaveArquivo)
                    .build();

            s3Client.copyObject(copyRequest);

            System.out.println("Arquivo copiado com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem do SQS: " + e.getMessage());
        }
    }

    private File downloadFileFromS3(String bucket, String key) throws IOException {
        System.out.printf("Baixando arquivo do S3: %s / %s", bucket, key);
        File tempFile = File.createTempFile("s3file", ".tmp");

        try (OutputStream os = new FileOutputStream(tempFile)) {
            s3Client.getObject(builder -> builder.bucket(bucket).key(key)).transferTo(os);
        }

        return tempFile;
    }

}
