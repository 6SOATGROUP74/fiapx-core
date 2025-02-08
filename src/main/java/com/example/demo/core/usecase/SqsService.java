package com.example.demo.core.usecase;

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

@Service
public class SqsService {

    private static final String SQS_URL = "http://localhost:4566/000000000000/minha-fila";
    private static final Logger logger = LoggerFactory.getLogger(SqsService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SqsTemplate sqsTemplate;

    private final S3Client s3Client;

    public SqsService(S3Client s3Client) {
        this.s3Client = s3Client;
    }


    public void enviaMensagem(String mensagem) {

        sqsTemplate.send(to -> to.queue("filaTeste").payload(mensagem));
    }

    public void enviarMensagemParaFila(String bucketName, String fileName, String bucketDestino) {
        // URL do arquivo no S3
        //String s3Url = String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
        //String s3Url = String.format("http://localhost:4566/%s/%s", bucketName, fileName);
        String s3Url = String.format("{\"bucketOrigem\": \"%s\", \"chaveArquivo\": \"%s\", \"bucketDestino\": \"%s\"}",
                bucketName, fileName, bucketDestino);

        // Enviar a URL para a fila do SQS
        sqsTemplate.send("filaTeste", s3Url);
    }

//    @SqsListener("filaTeste")
//    public void consomeEvento(String mensagem) {
//        logger.info("Mensagem recebida - {}", mensagem);
//    }

    @SqsListener("filaTeste")
    public void processarMensagem(String mensagem) {
        try {
            // Converter JSON para objeto
            JsonNode jsonNode = objectMapper.readTree(mensagem);
            //bucketOrigem
            String bucketOrigem = jsonNode.get("bucketOrigem").asText();

            //chaveArquivo
            String chaveArquivo = jsonNode.get("chaveArquivo").asText();

            //bucketDestino
            String bucketDestino = jsonNode.get("bucketDestino").asText();

            System.out.println("Copiando arquivo de " + bucketOrigem + " para " + bucketDestino);

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

}
